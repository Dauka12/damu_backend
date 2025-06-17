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
            if (course.get().getCourse_image() == null || course.get().getCourse_image().isEmpty()) {
                // Set default image URL or any other logic
                course.get().setCourse_image("data:image/png;base64,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");
            }
//            try {
//                if (organizationCategory.valueOf(course.get().getCourse_for_member_of_the_system()) != null) {
//                    course.get().setCourse_for_member_of_the_system(organizationCategory.valueOf(course.get().getCourse_for_member_of_the_system()).getCategory());
//                }
//                OrganizationCategory foundCategory = organizationCategory.valueOf(course.get().getCourse_for_member_of_the_system());
//                course.get().setCourse_for_member_of_the_system(foundCategory.getCategory());
//            } catch (IllegalArgumentException e) {
//
//            }
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
                    if (questions1.size() >= 20) {
                        // Shuffle the list
                        Collections.shuffle(questions1);

                        // Select the first 20 questions
                        questions = questions1.subList(0, 20);
                    }else {
                         questions = module.getQuiz().getQuizList();
                    }

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

            String[] russianText =
                    {"" ,
                            "" ,
                            StringUtils.center(fullName, 90) ,
                            StringUtils.center("«" + userCourse.getCourse().getCourse_name() + "»", 90),
                            StringUtils.center("қашықтан оқу форматында сәтті өткенін куәландырады", 90)  ,
                            StringUtils.center("(9 академиялық сағат)", 90) ,
                            "" ,
                            StringUtils.center("свидетельствует об успешном прохождении", 90) ,
                            StringUtils.center("«" + userCourse.getCourse().getCourse_name() + "»", 90)  ,
                            StringUtils.center("в дистанционном формате (9 академических часов)", 90) ,
                            "" ,
                            StringUtils.center("Ректор                                                            Мерзадинов Е.C.", 90) ,
                            "" ,
                            StringUtils.center("№" + rester + "           Берілген күні/ Дата получения: " + date, 90)};


            float x = 421;
            float y = 410;


            pdfContentByte.beginText();
            pdfContentByte.setFontAndSize(baseFont, 18);
 // Center of A4 size page
            for(int i=0;i<russianText.length;i++){
                pdfContentByte.showTextAligned(Element.ALIGN_CENTER, russianText[i], x, y-(i*24), 0);
            }
            pdfContentByte.endText();
            String url = "http://192.168.122.132:9000/api/checkQR/" + user.get().getUser_id() + "/" + course_id;
            byte[] qr = qrCodeGenerator.getQrCode(url, 100, 100);
            Image qrImage = Image.getInstance(qr);
            qrImage.setAbsolutePosition(71, 420);
            pdfContentByte.addImage(qrImage);

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
