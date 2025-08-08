package com.AFM.AML.Course.service;

import com.AFM.AML.Course.common.OrganizationCategory;
import com.AFM.AML.Course.models.*;
import com.AFM.AML.Course.models.DTOs.*;
import com.AFM.AML.Course.models.Module;
import com.AFM.AML.Course.repository.*;
import com.AFM.AML.Course.utils.ImageUtil;
import com.AFM.AML.Minio.service.MinioService;
import com.AFM.AML.Quiz.models.MatchingPair;
import com.AFM.AML.Quiz.models.Question;
import com.AFM.AML.Quiz.repository.Quiz_resultsRepository;
import com.AFM.AML.User.models.Log;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.awt.geom.Dimension2D;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import org.apache.commons.lang3.StringUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.*;
import java.security.Principal;
import java.util.Optional;
import io.minio.errors.*;
import org.hibernate.sql.exec.ExecutionException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {
    @Autowired
    CourseRepo courseRepo;
    @Autowired
    CourseCategoryRepo courseCategoryRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserCourseRepository userCourseRepository;
    @Autowired
    CourseCommentsRepo courseCommentsRepo;

    @Autowired
    ImageUtil imageUtil;
    //    @Autowired
//    MinioService minioService;
    @Autowired
    UserLessonRepo userChapterCheckRepo;

    OrganizationCategory organizationCategory;
    @Autowired
    MinioService minioService;
    @Autowired
    QrCodeGenerator qrCodeGenerator;
    @Autowired
    Quiz_resultsRepository quizResultsRepository;

    @Autowired
    invoiceidRepo invoiceidRepo;
    @Autowired
    PostLinkRepo postLinkRepo;


    public List<Integer> getUserBazovii(){
        List<UserCourse> userCourses = userCourseRepository.findBazovii();

        List<Integer> user_ids = new ArrayList<>();
        for(UserCourse userCourse: userCourses){
            user_ids.add(userCourse.getUser().getUser_id());
        }
        return user_ids;
    }

    public ResponseEntity<?> getUsersInvoice(Principal principal){
        Random random = new Random();

        long min = 100000; // 6 digits
        long max = 999999999999999L; // 15 digits
        long randomNumber = min + ((long) (random.nextDouble() * (max - min)));

        System.out.println("Random integer between 6 and 15 digits: " + randomNumber);
        Optional<User> user = userRepository.findByEmail(principal.getName());
        invoiceID iinvoiceID = new invoiceID();
        iinvoiceID.setInvoice(randomNumber);
        invoiceidRepo.save(iinvoiceID);
        Map<String, Object> data = new HashMap<>();
        data.put("invoice_id", randomNumber);
        data.put("email", principal.getName());
        data.put("user_id", user.get().getUser_id());
        return ResponseEntity.ok(data);
    }

    public ResponseEntity<?> createPostLink(PostLink postLink){
        postLinkRepo.save(postLink);
        return ResponseEntity.ok("ok");
    }

    public ResponseEntity<?> returnPostLink(String invoice_id){
        PostLink postLink = postLinkRepo.getPostLinksByInvoiceID(invoice_id);
        return ResponseEntity.ok(postLink);
    }

    public ResponseEntity<?> getUsersAndCourses(){
        List<User> users = userRepository.findAll();
        ModelMapper modelMapper = new ModelMapper();
        List<UserDTO> userDTOS = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
        List<Course> courses = courseRepo.findAvailable();
        List<CourseDTO> courseDTOS = courses.stream().map(course -> modelMapper.map(course, CourseDTO.class)).collect(Collectors.toList());
        HashMap<String,Object> usersAndCourse = new HashMap<>();
        usersAndCourse.put("users", userDTOS);
        usersAndCourse.put("courses", courseDTOS);
        return ResponseEntity.ok(usersAndCourse);
    }
    @Transactional
    public Optional<Course> getCourseByID(int course_id){
        Optional<Course> courses = courseRepo.findById(course_id);
        increaseCourseViews(course_id);
        return courses;
    }

    public CourseByIdDTO getCourseForUser(int course_id, Principal principal) {
        long startTime = System.currentTimeMillis();

        Optional<User> user = userRepository.findByEmail(principal.getName());
        UserCourse userCourse = userCourseRepository.findByUserAndCourseId(user.get().getUser_id(),course_id);
//        List<CourseComments> courseComments = courseCommentsRepo.findByComment();
        ModelMapper modelMapper = new ModelMapper();
        userCourse.getCourse().getCourseCategory().getCategory_image();
        List<Module> modules = userCourse.getCourse().getModules();
        List<String> right_parts =  new ArrayList<>();
        for(Module module : modules){
            List<Question> questions = module.getQuiz().getQuizList();
            for(Question question : questions){
                List<MatchingPair> matchingPairs = question.getMatchingPairs();
                for(MatchingPair matchingPair : matchingPairs){
                    String sds = matchingPair.getRightPart();
                    right_parts.add(sds);
                }
                Collections.shuffle(right_parts);
                for (int i = 0; i < matchingPairs.size(); i++) {
                    // Access the MatchingPair using the index 'i' and set the rightPart
                    matchingPairs.get(i).setRightPart(right_parts.get(i));
                }
            }
        }
        CourseByIdDTO courseByIdDTO1 = modelMapper.map(userCourse, CourseByIdDTO.class);
//            courseByIdDTO1.getCourse().setCourseComments(courseComments);
        courseByIdDTO1.setCourse(userCourse.getCourse());
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("Execution time: " + executionTime + "ms");
        return courseByIdDTO1;
    }

//    public ResponseEntity<Object> getCatalog() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        List<Course> courses = courseRepo.findByDeleted();
//
//        List<Course> newCourses = new ArrayList<>();
//        for (Course course : courses) {
//            course.setCourse_image(minioService.getFileUrl(course.getCourse_image()));
//            try {
//                if (organizationCategory.valueOf(course.getCourse_for_member_of_the_system()) != null) {
//                    course.setCourse_for_member_of_the_system(organizationCategory.valueOf(course.getCourse_for_member_of_the_system()).getCategory());
//                }
//                OrganizationCategory foundCategory = organizationCategory.valueOf(course.getCourse_for_member_of_the_system());
//                course.setCourse_for_member_of_the_system(foundCategory.getCategory());
//                course.setCourse_image(minioService.getFileUrl(course.getCourse_image()));
//            } catch (IllegalArgumentException e) {
//
//            }
//
//            newCourses.add(course);
//        }
//
//        return ResponseEntity.ok(newCourses);
//    }

    public ResponseEntity<Object> getCatalog() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<CourseWithoutModuleDTO> courses = courseRepo.findByDeletedWithoutModule();

        List<CourseWithoutModuleDTO> newCourses = new ArrayList<>();
        for (CourseWithoutModuleDTO course : courses) {
            course.setCourse_image(minioService.getFileUrl(course.getCourse_image()));
            try {
                if (organizationCategory.valueOf(course.getCourse_for_member_of_the_system()) != null) {
                    course.setCourse_for_member_of_the_system(organizationCategory.valueOf(course.getCourse_for_member_of_the_system()).getCategory());
                }
                OrganizationCategory foundCategory = organizationCategory.valueOf(course.getCourse_for_member_of_the_system());
                course.setCourse_for_member_of_the_system(foundCategory.getCategory());
                course.setCourse_image(minioService.getFileUrl(course.getCourse_image()));
            } catch (IllegalArgumentException e) {

            }

            newCourses.add(course);
        }

        return ResponseEntity.ok(newCourses);
    }




    public ResponseEntity<Object> getCourseBasicInfo(int id) {
        Optional<Course> course = courseRepo.findById(id);
        if (course.isPresent()) {
            return ResponseEntity.ok(course.get());
        }
        return ResponseEntity.badRequest().body("NO COURSE FOUND");
    }

    public ResponseEntity<Object> updateCourseBasicInfo(CourseBasicInfoDTO courseDTO, int id) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Optional<Course> course = courseRepo.findById(id);

        if (course.isPresent()) {
            course.get().setCourse_name(courseDTO.getTitle());
            course.get().setCourse_price(Double.parseDouble(courseDTO.getPrice()));
            course.get().setLanguage(courseDTO.getLanguage());

            try{
                MultipartFile file= imageUtil.base64ToMultipartFile(courseDTO.getImage());
                if(file!=null){
                    String filename=UUID.randomUUID().toString();
                    minioService.uploadFile(file,filename);
                    courseDTO.setImage(filename);
                }
            }   catch (Exception e){
                System.out.println(e.getMessage());
            }
            course.get().setCourse_image(courseDTO.getImage());
            course.get().setCourse_for_member_of_the_system(courseDTO.getAudience());
            course.get().setDraft(true);

            courseRepo.save(course.get());

            return ResponseEntity.ok(course.get().getCourse_id());
        }
        return ResponseEntity.badRequest().body("NO COURSE FOUND");

    }
    public ResponseEntity<Object> updateCourseNoBasicInfo(CourseNoBasicInfoDTO courseDTO, int id) {
        Optional<Course> course = courseRepo.findById(id);
        if (course.isPresent()) {
            course.get().setWhat_course_represents(courseDTO.getWhat_course_represents());
            course.get().setWhat_is_agenda_of_course(courseDTO.getWhat_is_agenda_of_course());
            course.get().setWhat_is_availability(courseDTO.getWhat_is_availability());
            course.get().setWhat_is_duration(courseDTO.getWhat_is_duration());
            course.get().setWhat_you_will_get(courseDTO.getWhat_you_will_get());
            course.get().setWho_course_intended_for(courseDTO.getWho_course_intended_for());
            course.get().setDraft(true);

            courseRepo.save(course.get());

            return ResponseEntity.ok(course.get().getCourse_id());
        }
        return ResponseEntity.badRequest().body("NO COURSE FOUND");
    }

    public ResponseEntity<Object> publishCourse(int id) {
        Optional<Course> course = courseRepo.findById(id);
        if (course.isPresent()) {
            course.get().setDraft(false);
            courseRepo.save(course.get());
            return ResponseEntity.ok(course.get());
        }
        return ResponseEntity.badRequest().body("NO COURSE FOUND");
    }

    public ResponseEntity<Object> deleteCourse(int id) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Optional<Course> course = courseRepo.findById(id);

        if (course.isPresent()) {
            course.get().setDeleted(true);
            courseRepo.save(course.get());
        }
        return ResponseEntity.ok(getCatalog());
    }



    public ResponseEntity<Course> createDraftBasicInfo(CourseBasicInfoDTO courseDTO) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (courseDTO == null) {
            return null;
        }

        Course course = new Course();
        course.setCourse_name(courseDTO.getTitle());
        course.setCourse_price(Double.parseDouble(courseDTO.getPrice()));
//        String filename=UUID.randomUUID().toString();
//
//        System.out.println(minioService.uploadFile(courseDTO.getImage(), filename));
        course.setCourse_image(courseDTO.getImage());
        course.setCourse_for_member_of_the_system(courseDTO.getAudience());
        course.setLanguage(courseDTO.getLanguage());
        course.setDraft(true);

        return saveCourse(course, Integer.parseInt(courseDTO.getCategory()));
    }



    public void saveCourseCategory(CourseCategory courseCategory){
        courseCategoryRepo.save(courseCategory);
    }
    public void createCourseComments(CourseComments courseComments,Principal principal,int course_id){
        Optional<User> user = userRepository.findByEmail(principal.getName());
        Optional<Course> course = courseRepo.findById(course_id);
        courseComments.setUser(user.get());
        courseComments.setCourse(course.get());
        Log log = new Log();
        log.setDescription(principal.getName() + " commented" + course_id + " course");
        log.setActivity("comment");
        Date date = new Date();
        log.setDate(date);
        courseCommentsRepo.save(courseComments);
    }
    public ResponseEntity<?> updateCourseCategory(CourseCategory courseCategory){
        return ResponseEntity.ok(courseCategoryRepo.save(courseCategory));
    }

    public ResponseEntity<?> deleteCourseCategory(int course_category_id){
        courseCategoryRepo.deleteById(course_category_id);
        return ResponseEntity.ok("vse zbs");
    }

    public ResponseEntity<Course> saveCourse(Course course,int courseCategoryId){
        courseCategoryRepo.findById(courseCategoryId).map(courseCategory ->
        {
            course.setCourseCategory(courseCategory);
            return courseRepo.save(course);
        }).orElseThrow(() -> new ExecutionException("Not found CourseCategory with id = " + courseCategoryId));
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }

    public void saveCourse(Course course){
        courseRepo.save(course);
    }

//    public ResponseEntity<?> saveCourseAvailableForUser(User user){
//        List<Course> courses = courseRepo.findAll();
//        for(Course course: courses){
//            UserCourse userCourse = new UserCourse();
//            userCourse.setUser(user);
//            userCourse.setCourse(course);
//            userCourse.setPayment_type("KASPI.KZ");
//            userCourse.setStatus("available");
//            userCourseRepository.save(userCourse);
//        }
//        return ResponseEntity.ok("OK");
//    }
    public Optional<Course> previewCourse(int id) {
        return courseRepo.findById(id);
    }

    public ResponseEntity<?> addUsertoCourse(int userId, int courseId){
            Optional<User> user = userRepository.findById(userId);
            Optional<Course> course = courseRepo.findById(courseId);
        // Check if the user is already enrolled in the course
            Optional<UserCourse> existingEnrollment = Optional.ofNullable(userCourseRepository.findByUserAndCourseId(userId, courseId));
            if (existingEnrollment.isPresent()) {
                return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("User is already enrolled in this course");
            }
            List<Module> modules = course.get().getModules();
            for(Module module : modules){
//                System.out.println(chapter1);
               List<Lesson> lessons = module.getLessons();
               for(Lesson lesson : lessons){
                   UserLessonCheck userChapterCheck = new UserLessonCheck();
                   userChapterCheck.setChecked(false);
                   userChapterCheck.setUser(user.get());
                   userChapterCheck.setLesson(lesson);
                   userChapterCheckRepo.save(userChapterCheck);
               }
            }
            UserCourse userCourse = new UserCourse();
            userCourse.setCourse(course.get());
            userCourse.setUser(user.get());
            userCourse.setPayment_type("KASPI");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            userCourse.setPayment_date(date);
            userCourse.setProgress_percentage(0);
            userCourse.setStatus("process");
            userCourseRepository.save(userCourse);
        userCourseRepository.findUserCourseByCourseAndUser(userId,courseId);
        return ResponseEntity.ok().body("ok");
    }
    public ResponseEntity<?> addUserstoCourse() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if ("Правоохранительные органы".equals(user.getMember_of_the_system())) {
//            Optional<User> user = userRepository.findById(userId);
                Optional<Course> course = courseRepo.findById(50);
//            CourseDTO courseDTO = CourseDTO.builder().course_id(course.get().getCourse_id()).
//                    course_price(course.get().getCourse_price()).course_name(course.get().getCourse_name()).
//                    course_image(course.get().getCourse_image()).build();
//            userCourse.setCourse(courseDTO);
                List<Module> modules = course.get().getModules();
                for (Module module : modules) {
//                System.out.println(chapter1);
                    List<Lesson> lessons = module.getLessons();
                    for (Lesson lesson : lessons) {
                        UserLessonCheck userChapterCheck = new UserLessonCheck();
                        userChapterCheck.setChecked(false);
                        userChapterCheck.setUser(user);
                        userChapterCheck.setLesson(lesson);
                        userChapterCheckRepo.save(userChapterCheck);
                    }
                }
                UserCourse userCourse = new UserCourse();
                userCourse.setCourse(course.get());
                userCourse.setUser(user);
                userCourse.setPayment_type("KASPI");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                userCourse.setPayment_date(date);
                userCourse.setProgress_percentage(0);
                userCourse.setStatus("process");
                userCourseRepository.save(userCourse);
                userCourseRepository.findUserCourseByCourseAndUser(user.getUser_id(), 50);
            }
        }
        return ResponseEntity.ok().body("ok");
    }
    public ResponseEntity<?> addUsertoCourseWithCount(int courseId,String model) throws JsonProcessingException {
//            Optional<User> user = userRepository.findById(30);
            Optional<Course> course = courseRepo.findById(courseId);
//            CourseDTO courseDTO = CourseDTO.builder().course_id(course.get().getCourse_id()).
//                    course_price(course.get().getCourse_price()).course_name(course.get().getCourse_name()).
//                    course_image(course.get().getCourse_image()).build();
//            userCourse.setCourse(courseDTO);
              ObjectMapper objectMapper = new ObjectMapper();
              UserCourse userCourse = objectMapper.readValue(model, UserCourse.class);
            userCourse.setCourse(course.get());
//            userCourse.setUser(user.get());
//            userCourse.setUser(user.get());
            userCourse.setPayment_type("request");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            userCourse.setPayment_date(date);
            userCourse.setStatus("request");
            userCourseRepository.save(userCourse);
//            courseRepo.getCountOfRequest(courseId);
//            Integer integer = courseRepo.getCountOfRequest(courseId);
            Integer integer = userCourseRepository.getCountOfRequest(courseId);
            course.get().setRating(Double.valueOf(integer));
            courseRepo.save(course.get());
        return ResponseEntity.ok().body("ok");
    }

        private void increaseCourseViews(int courseId) {
            courseRepo.incrementViews(courseId);
        }
    public CourseByIdDTO findCourseByIdd(Principal principal, int course_id) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
            Optional<User> user = Optional.of(new User());
            UserCourse userCourse = new UserCourse();
            user = userRepository.findByEmail(principal.getName());
            userCourse = userCourseRepository.findByUserAndCourseId(user.get().getUser_id(), course_id);
            if (course_id == 86||course_id==104) {
                user = userRepository.findByEmail("damir_ps@mail.ru");
                userCourse = userCourseRepository.findByUserAndCourseId(user.get().getUser_id(), 86);
            }

//        List<CourseComments> courseComments = courseCommentsRepo.findByComment();
        ModelMapper modelMapper = new ModelMapper();
            List<Module> modules = userCourse.getCourse().getModules();
            Iterator<Module> iterator = modules.iterator();
            while (iterator.hasNext()) {
                Module module = iterator.next();
                if (!module.is_active()) {
                    iterator.remove();  // Remove the module if it is not active
                }
                Iterator<Lesson> lessonIterator = module.getLessons().iterator();
                while (lessonIterator.hasNext()){
                    System.out.printf("effefefefefefefef");
                    Lesson lesson = lessonIterator.next();
                    if(!lesson.is_active()){
                        lessonIterator.remove();
                    }
                }
            }
            userCourse.getCourse().getCourseCategory().getCategory_image();

            for(Module module : modules) {
                System.out.println(module.is_active());

                if (module.getQuiz() != null) {
                    List<Question> questions1 = module.getQuiz().getQuizList();
                    List<Question> questions = new ArrayList<>();
                    questions = module.getQuiz().getQuizList();


                    module.getQuiz().setQuizList(questions);
                    if (quizResultsRepository.checkIsChecksAccept(user.get().getUser_id(), module.getQuiz().getQuiz_id()) == true) {
                        System.out.println(module.getQuiz().getQuiz_id());
                        module.getQuiz().setQuiz_max_points(100.0);
                    }
                    for (Question question : questions) {
                        if (question.getMatchingPairs() != null) {
                            List<MatchingPair> matchingPairs = question.getMatchingPairs();
                            List<String> right_parts =  new ArrayList<>();
                            for (MatchingPair matchingPair : matchingPairs) {
                                String sds = matchingPair.getRightPart();
                                right_parts.add(sds);
                            }
                            Collections.shuffle(right_parts);
                            for (int i = 0; i < matchingPairs.size(); i++) {
                                // Access the MatchingPair using the index 'i' and set the rightPart
                                matchingPairs.get(i).setRightPart(right_parts.get(i));
                            }
                        }
                    }
                }
            }
            CourseByIdDTO courseByIdDTO1 = modelMapper.map(userCourse, CourseByIdDTO.class);
//            courseByIdDTO1.getCourse().setCourseComments(courseComments);
            courseByIdDTO1.setCourse(userCourse.getCourse());

        return courseByIdDTO1;
    }
    public ResponseEntity<?> getUserCourse(Principal principal, int course_id){
        Optional<User> user = userRepository.findByEmail(principal.getName());
        UserCourse userCourse = userCourseRepository.findByUserAndCourseId(user.get().getUser_id(),course_id);
        return ResponseEntity.ok(userCourse);
    }
    public List<UserCourseDTO> findUsersCourses(Principal principal) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Optional<User> user = userRepository.findByEmail(principal.getName());
        List<UserCourseDTO> catalog = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        if (user.isPresent()) {
            List<Course> courses = courseRepo.findAvailable();
            for (Course course: courses) {
                UserCourseDTO catalogItem = new UserCourseDTO();
                CourseWithoutChapter courseWithoutChapter = modelMapper.map(course, CourseWithoutChapter.class);
                courseWithoutChapter.setCourse_image(minioService.getFileUrl(course.getCourse_image()));
                catalogItem.setCourseDTO(courseWithoutChapter);
                catalogItem.setLanguage(course.getLanguage());
                Optional<UserCourse> paymentInfo = userCourseRepository.findPaymentInfo(user.get().getUser_id(), course.getCourse_id());
                if (paymentInfo.isPresent()) {
                    PaymentInfoDTO paymentInfoDTO = modelMapper.map(paymentInfo.get(), PaymentInfoDTO.class);
                    catalogItem.setPaymentInfo(paymentInfoDTO);
                    if (paymentInfoDTO.getStatus().equals("process")) {
                        catalogItem.setShortStatus(2);
                    } else {
                        catalogItem.setShortStatus(3);
                    }
                }
                catalog.add(catalogItem);
            }
        }
        return catalog;
    }
    public List<UserCourseDTO> findUsersCoursesNoPr() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<UserCourseDTO> catalog = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        List<Course> courses = courseRepo.findAvailable();
        for (Course course: courses) {
            UserCourseDTO catalogItem = new UserCourseDTO();
            CourseWithoutChapter courseWithoutChapter = modelMapper.map(course, CourseWithoutChapter.class);
            courseWithoutChapter.setCourse_image(minioService.getFileUrl(course.getCourse_image()));
            catalogItem.setCourseDTO(courseWithoutChapter);
            catalogItem.setLanguage(course.getLanguage());
            catalogItem.setShortStatus(1);
            catalog.add(catalogItem);
        }
        return catalog;
    }

    public List<UserCourseDTO> getProcessingCourses(Principal principal) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Optional<User> user = userRepository.findByEmail(principal.getName());
        List<UserCourseDTO> catalog = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        if (user.isPresent()) {
            List<Integer> courses = userCourseRepository.findByProcessAndFinished(user.get().getUser_id());

            List<Integer> specificIntegers = List.of(8,81);
            List<Integer> resultList = new ArrayList<>();
            for (Integer num : new ArrayList<>(courses)) { // Use a copy to avoid ConcurrentModificationException
                if (specificIntegers.contains(num)) {
                    resultList.add(num);
                    courses.remove(num);
                }
            }
            courses.addAll(0, resultList);


            for (int courseID: courses) {
                Optional<Course> course = courseRepo.findById(courseID);

                if (course.isPresent()) {
                    UserCourseDTO catalogItem = new UserCourseDTO();
                    CourseWithoutChapter courseWithoutChapter = modelMapper.map(course, CourseWithoutChapter.class);
                    courseWithoutChapter.setCourse_image(minioService.getFileUrl(course.get().getCourse_image()));
                    catalogItem.setCourseDTO(courseWithoutChapter);
                    Optional<UserCourse> paymentInfo = userCourseRepository.findPaymentInfo(user.get().getUser_id(), course.get().getCourse_id());
                    if (paymentInfo.isPresent()) {
                        PaymentInfoDTO paymentInfoDTO = modelMapper.map(paymentInfo.get(), PaymentInfoDTO.class);
                        catalogItem.setPaymentInfo(paymentInfoDTO);
                        if (paymentInfoDTO.getStatus().equals("process")) {
                            catalogItem.setShortStatus(2);
                        } else {
                            catalogItem.setShortStatus(3);
                        }
                    }
                    catalog.add(catalogItem);
                }
            }
        }
        return catalog;
    }
    public ResponseEntity<byte[]> editPdf(Principal principal, int course_id) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/ceee.pdf");
            PdfReader pdfReader = new PdfReader(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfStamper pdfStamper = new PdfStamper(pdfReader, byteArrayOutputStream);
            PdfContentByte pdfContentByte = pdfStamper.getOverContent(1);

            InputStream fontStream = getClass().getResourceAsStream("/ARIALUNI.TTF");
            BaseFont baseFont = BaseFont.createFont("ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontStream.readAllBytes(), null);

            Font font = new Font(baseFont, 18);
            Optional<User> user = userRepository.findByEmail(principal.getName());
            UserCourse userCourse = userCourseRepository.findByUserAndCourseId(user.get().getUser_id(), course_id);
            String date = userCourse.getDate_certificate();
            int rester = userCourse.getCertificate_int();
            String fullName = userCourse.getStatic_full_name();

            // Разбиваем название курса на строки если оно слишком длинное
            String courseName = userCourse.getCourse().getCourse_name();
            List<String> courseNameLines = new ArrayList<>();
            
            // Разбиваем текст на строки по 45 символов (уменьшил для лучшего отображения)
            int maxLineLength = 45;
            if (courseName.length() <= maxLineLength) {
                courseNameLines.add(courseName);
            } else {
                String[] words = courseName.split(" ");
                StringBuilder currentLine = new StringBuilder();
                
                for (String word : words) {
                    if (currentLine.length() + word.length() + 1 <= maxLineLength) {
                        if (currentLine.length() > 0) {
                            currentLine.append(" ");
                        }
                        currentLine.append(word);
                    } else {
                        if (currentLine.length() > 0) {
                            courseNameLines.add(currentLine.toString());
                            currentLine = new StringBuilder(word);
                        } else {
                            // Если одно слово длиннее максимальной длины, обрезаем его
                            if (word.length() > maxLineLength) {
                                courseNameLines.add(word.substring(0, maxLineLength - 3) + "...");
                            } else {
                                courseNameLines.add(word);
                            }
                        }
                    }
                }
                if (currentLine.length() > 0) {
                    courseNameLines.add(currentLine.toString());
                }
            }
            
            // Создаем массив текста с учетом переносов строк
            List<String> textLines = new ArrayList<>();
            textLines.add(""); // пустая строка
            textLines.add(""); // пустая строка
            
            // ФИО
            textLines.add(StringUtils.center(fullName, 90));
            textLines.add(""); // пустая строка
            
            // Название курса на казахском
            for (String line : courseNameLines) {
                textLines.add(StringUtils.center(line, 60));
            }
            
            // Казахский текст
            textLines.add(StringUtils.center("курсынан қашықтықтан оқу форматында сәтті өткенін растайды", 60));
            textLines.add(""); // пустая строка
            
            // Русский текст
            textLines.add(StringUtils.center("подтверждает успешное прохождение курса", 60));
            
            // Название курса на русском
            for (String line : courseNameLines) {
                textLines.add(StringUtils.center(line, 60));
            }
            
            textLines.add(StringUtils.center("в дистанционном формате", 60));
            textLines.add(""); // пустая строка
            textLines.add(""); // пустая строка
            textLines.add(StringUtils.center("Берілген күні/ Дата получения: " + date, 60));

            String[] russianText = textLines.toArray(new String[0]);

            // Определяем размер шрифта в зависимости от количества строк
            int fontSize;
            if (courseNameLines.size() <= 1) {
                fontSize = 16; // Еще больше увеличиваем размер для коротких названий
            } else if (courseNameLines.size() <= 2) {
                fontSize = 15; // Для 2 строк
            } else if (courseNameLines.size() <= 3) {
                fontSize = 14; // Для 3 строк
            } else {
                fontSize = 13; // Для длинных названий
            }

            float x = 520; // Сдвигаем еще правее (было 400)
            float y = 360; // Сдвигаем вниз, чтобы текст был ниже слова "Ведомость" (было 480)

            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(baseFont, fontSize);
            // Center of A4 size page
            for(int i=0;i<russianText.length;i++){
                pdfContentByte.showTextAligned(Element.ALIGN_CENTER, russianText[i], x, y-(i*22), 0);
            }
            pdfContentByte.endText();
            String url = "http://192.168.122.132:9000/api/checkQR/" + user.get().getUser_id() + "/" + course_id;
            byte[] qr = qrCodeGenerator.getQrCode(url, 100, 100);
            Image qrImage = Image.getInstance(qr);
            // Позиционируем QR-код в правом нижнем углу
            qrImage.setAbsolutePosition(710, 40); // x=480 (правее), y=60 (снизу)
            pdfContentByte.addImage(qrImage);

            // Добавляем номер сертификата под QR-кодом
            String certificateNumber = "№ " + user.get().getUser_id() + "-" + course_id + "-" + rester;
            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(baseFont, 8);
            pdfContentByte.showTextAligned(Element.ALIGN_CENTER, "Номер сертификата:", 760, 30, 0);
            pdfContentByte.showTextAligned(Element.ALIGN_CENTER, certificateNumber, 760, 18, 0);
            pdfContentByte.endText();

            pdfStamper.close();
            pdfReader.close();

            return ResponseEntity
                    .ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(byteArrayOutputStream.toByteArray());
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Course> findCourseById(int id){
        return courseRepo.findById(id);
    }

    public List<CourseCategory> getCourseCategory() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<CourseCategory> courseCategories = courseCategoryRepo.findAll();
        return courseCategories;
    }
    public List<Course> getCourses() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<Course> courses = courseRepo.findAll();
        return courses;
    }

    public void deleteCourse(Integer id){
        courseRepo.deleteById(id);
    }

    public Page<Course> getCourseByPageable(Pageable pageable){
        return courseRepo.findAll(pageable);
    }

    public List<UserCourse> getUsersAndCoursesRequest() {
       return userCourseRepository.findByUserAndCourseIdRequest();
    }
}
