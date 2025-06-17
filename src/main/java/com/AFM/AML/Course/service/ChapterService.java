package com.AFM.AML.Course.service;

import com.AFM.AML.Course.models.*;
import com.AFM.AML.Course.models.DTOs.LessonDTO;
import com.AFM.AML.Course.models.DTOs.ModuleDTO;
import com.AFM.AML.Course.models.DTOs.UserLessonCheckDTO;
import com.AFM.AML.Course.models.Module;
import com.AFM.AML.Course.repository.*;
import com.AFM.AML.Course.utils.ImageUtil;
import com.AFM.AML.Minio.service.MinioService;
import com.AFM.AML.Quiz.repository.Quiz_resultsRepository;
import com.AFM.AML.User.exception.NotFoundException;
import com.AFM.AML.User.models.Log;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.*;
import jakarta.transaction.Transactional;
import jakarta.xml.bind.DatatypeConverter;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ChapterService {
    @Autowired
    ChapterRepo chapterRepo;
    @Autowired
    CourseRepo courseRepo;
    @Autowired
    CourseCategoryRepo courseCategoryRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    Sub_chapterRepo subChapterRepo;
    @Autowired
    Sub_chapter_materials_repo subChapterMaterialsRepo;
    @Autowired
    UserLessonRepo userLessonRepo;
    @Autowired
    ComponentEntryRepo componentEntryRepo;
    @Autowired
    ModuleRepo moduleRepo;
    @Autowired
    LessonRepo lessonRepo;
    @Autowired
    UserCourseRepository userCourseRepository;
    @Autowired
    Quiz_resultsRepository quizResultsRepository;
    @Autowired
    ComponentEntryValuesRepo componentEntryValuesRepo;
    @Autowired
    ImageUtil imageUtil;
    @Autowired
    MinioService minioService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseEntity<?> getAllComponentEntries() throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<ComponentEntryValues> componentEntryValues = componentEntryValuesRepo.getComponentEntriesByPhoto();
        for(ComponentEntryValues componentEntryValues1 : componentEntryValues) {
            String base64String = componentEntryValues1.getValues().get("img");
            String[] parts = base64String.split(",");
            String contentType = parts[0].split(";")[0].split(":")[1];
            String base64Data = parts[1];
            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(base64Data);
            MultipartFile file = new MockMultipartFile("file", "filename", contentType, new ByteArrayInputStream(decodedBytes));
            String fileName = UUID.randomUUID().toString();
            String path = fileName;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("fdsf");
                minioService.uploadFile(file, path);
                componentEntryValues1.getValues().put("img", "http://192.168.122.132:9000/aml/" + path);
                componentEntryValuesRepo.save(componentEntryValues1);
        }
        return ResponseEntity.ok().body("d");
    }

    public MultipartFile convert(String base64String) throws IOException {
        // Split the base64 string into its data and metadata parts
        String[] parts = base64String.split(",");

        // Extract the content type and base64 data
        String contentType = parts[0].split(";")[0].split(":")[1];
        String base64Data = parts[1];

        // Decode the base64 data
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(base64Data);

        // Create a MultipartFile instance
        return new MockMultipartFile("file", "filename", contentType, new ByteArrayInputStream(decodedBytes));
    }

    public ResponseEntity<Object> getComponents(int id) {
        Optional<Lesson> lesson = lessonRepo.findById(id);
        if (lesson.isPresent()) {
            List<ComponentEntry> componentEntries = componentEntryRepo.findByLessonId(id);
            return ResponseEntity.ok(componentEntries);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("GivenLesson was not found");
    }
    @Transactional
    public void saveComponents(List<Object> componentHistory, int id) throws Exception {
        Optional<Lesson> lessonOpt = lessonRepo.findById(id);
        if (lessonOpt.isEmpty()) {
            throw new NotFoundException("Lesson with id " + id + " was not found.");
        }

        Lesson lesson = lessonOpt.get();
        List<ComponentEntry> existingEntries = componentEntryRepo.findByLessonId(id);
        for (Object component : componentHistory) {
            ComponentEntry componentEntry = convertToComponentEntry(component, lesson);
            componentEntryRepo.save(componentEntry);
        }
        componentEntryRepo.deleteAll(existingEntries);
    }

    private ComponentEntry convertToComponentEntry(Object component, Lesson lesson) throws Exception {
        String json = objectMapper.writeValueAsString(component);
        Map<String, Object> componentMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        ComponentEntry componentEntry = new ComponentEntry();
        componentEntry.setComponentName((String) componentMap.get("componentName"));
        componentEntry.setLesson(lesson);

        Map<String, Object> values = (Map<String, Object>) componentMap.get("values");
        ComponentEntryValues componentEntryValues = new ComponentEntryValues();
        Map<String, String> parsedValues = new HashMap<>();

        HashSet<String> base64filetypes=new HashSet<>();
        base64filetypes.add("img");
        base64filetypes.add("image");
        base64filetypes.add("file");

        HashSet<String> arrayTypes=new HashSet<>();
        arrayTypes.add("images");
        arrayTypes.add("icons");

        HashSet<String> jsonbase64filetypes=new HashSet<>();
        jsonbase64filetypes.add("stages");
        jsonbase64filetypes.add("data");

        HashSet<String> dynamictypes=new HashSet<>();
        dynamictypes.add("left");
        dynamictypes.add("right");

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            String filename= String.valueOf(entry.getValue()) ;
            if(base64filetypes.contains(entry.getKey())){
                filename=imageUtil.imgAndFiles(String.valueOf(entry.getValue()));
            }else if(arrayTypes.contains(entry.getKey())){
                filename=imageUtil.images(String.valueOf(entry.getValue()));
            }
            else if(jsonbase64filetypes.contains((entry.getKey()))){
                filename= imageUtil.jsonFormImages(String.valueOf(entry.getValue()),entry.getKey());
            }
            else if(dynamictypes.contains(entry.getKey())){
                filename=imageUtil.dynamicTypes(String.valueOf(entry.getValue()));
            }
            entry.setValue(filename);
            parsedValues.put(entry.getKey(), String.valueOf(entry.getValue()));
        }

        componentEntryValues.setValues(parsedValues);
        componentEntry.setValues(componentEntryValues);

        return componentEntry;
    }
    public ResponseEntity<Object> getModulesByCourseId(int id) {
        try {
            List<Module> modules = moduleRepo.findByCourseIdAndActive(id);
            return ResponseEntity.ok(modules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
        }
    }

    //Lessons aka Sub_chapters
    public ResponseEntity<Object> deleteLesson(int id) {
        try {
            Optional<Lesson> optionalLesson = lessonRepo.findById(id);

            if (optionalLesson.isPresent()) {
                Lesson lesson = optionalLesson.get();
                lesson.set_active(false);

                lessonRepo.save(lesson);

                List<Lesson> lessons = lessonRepo.findByModuleIdAndActive(lesson.getModule().getModule_id());
                return ResponseEntity.ok(lessons);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<Object> addLesson(Map<String, Object> body) {
        try {
            int id = Integer.parseInt(body.get("id").toString());
            String newLessonName = body.get("newLessonName").toString();
            Optional<Module> optionalModule = moduleRepo.findById(id);
            if (optionalModule.isPresent()) {
                Lesson lesson = new Lesson();
                lesson.setTopic(newLessonName);
                lesson.setModule(optionalModule.get());
                lesson.set_active(true);

                lessonRepo.save(lesson);

                return ResponseEntity.ok(lessonRepo.findByModuleIdAndActive(id));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
        } catch (NumberFormatException | NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    public ResponseEntity<Object> deleteModule(int id) {
        try {
            Optional<Module> optionalModule = moduleRepo.findById(id);

            if (optionalModule.isPresent()) {
                Module module = optionalModule.get();
                module.set_active(false);

                moduleRepo.save(module);

                List<Module> modules = moduleRepo.findByCourseIdAndActive(module.getCourse().getCourse_id());
                return ResponseEntity.ok(modules);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<Object> addModule(Map<String, Object> body) {
        try {
            int id = Integer.parseInt(body.get("id").toString());
            String newModuleName = body.get("newModuleName").toString();
            Optional<Course> course = courseRepo.findById(id);
            if (course.isPresent()) {
                System.out.println(course.get().getCourse_name());
                Module module = new Module();
                module.setName(newModuleName);
                module.setCourse(course.get());
                module.set_active(true);

                moduleRepo.save(module);

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }
            return ResponseEntity.ok(moduleRepo.findByCourseIdAndActive(id));

        } catch (NumberFormatException | NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<Object> getLessons(int id) {
        try {
            Optional<Module> module = moduleRepo.findById(id);

            if (module.isPresent()) {
                ModuleDTO moduleDTO = new ModuleDTO();
                moduleDTO.setChapter_id(module.get().getModule_id());
                moduleDTO.setChapter_name(module.get().getName());
                moduleDTO.setIs_active(module.get().is_active());
                List<LessonDTO> lessonDTOS = new ArrayList<>();
                List<Lesson> lessons = lessonRepo.findByModuleIdAndActive(id);
                for (Lesson lsn: lessons) {
                    LessonDTO lesson = new LessonDTO();
                    lesson.setSub_chapter_id(lsn.getLesson_id());
                    lesson.setSub_chapter_name(lsn.getTopic());
                    lesson.setIs_active(lsn.is_active());

                    lessonDTOS.add(lesson);
                }
                moduleDTO.setLessons(lessonDTOS);
                return ResponseEntity.ok(module);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log the exception for debugging
            System.out.println("Error while retrieving lessons" + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }





    public String createSub_chapter(Sub_chapter subChapter, int chapter_id){
        Optional<Chapter> chapter = chapterRepo.findById(chapter_id);
        subChapter.setChapter(chapter.get());
        subChapterRepo.save(subChapter);
        return "sub_chapter" + chapter + " was saved";
    }


    public String saveChapter(Chapter chapter, int course_id){
        Optional<Course> course = courseRepo.findById(course_id);
        chapter.setCourse(course.get());
        chapterRepo.save(chapter);
        return "chapter " + chapter + " was saved";
    }

    public ResponseEntity<?> checkedSubChapter(Principal principal, int lesson_id){
        Log log = new Log();
        log.setDescription(principal.getName() + " checked" + lesson_id + " lesson");
        log.setActivity("checked subchapter");
        Date date = new Date();
        log.setDate(date);
        Optional<User> user = userRepository.findByEmail(principal.getName());
        userLessonRepo.checked(user.get().getUser_id(),lesson_id);
        Course course = courseRepo.findByLessonId(lesson_id);
        int completedUserCount = (int) this.userCourseRepository.countAllByCourseAndStatus(course, "finished");
        course.setCompleted_users(completedUserCount);
        System.out.println(course.getCourse_id());
        UserCourse userCourse = userCourseRepository.findByUserAndCourseId(user.get().getUser_id(),course.getCourse_id());
        int lessons_by_course = 0;
        int checked_lessons = 0;
        if(course.getModules() != null) {
            List<Module> module = course.getModules();
            for (Module module1 : module) {
                if (module1.is_active() == true) {
                    System.out.println(module1.getModule_id());
                    checked_lessons += userLessonRepo.countOfCheckedLessons(user.get().getUser_id(), module1.getModule_id());
//                    System.out.println(module1.getModule_id());
//                    System.out.println(userChapterCheckRepo.getUserLessonCheckByModuleId(module1.getModule_id()));

                    for (Lesson lesson : module1.getLessons()) {
                        if (lesson.is_active()) {
                            lessons_by_course++;
                        }
                    }
                    if (module1.getQuiz() != null) {
                        lessons_by_course += 1;
                        checked_lessons += quizResultsRepository.countOfFinishedTestsofUser(user.get().getUser_id(), module1.getQuiz().getQuiz_id());
                    }
                }
            }
            UniqueIntegerGenerator integerGenerator = new UniqueIntegerGenerator();
            if ((checked_lessons * 100) / lessons_by_course >= 100.0) {
                userCourse.setStatus("finished");
                userCourse.setProgress_percentage(100.0);
                int integerr = integerGenerator.getNextUniqueInteger();
                userCourse.setCertificate_int(integerr);
                LocalDate currentLocalDate = LocalDate.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // Adjust the format as needed
                String formattedDate = currentLocalDate.format(dateFormatter);
                userCourse.setDate_certificate(formattedDate);
                userCourse.setStatic_full_name(user.get().getLastname() + " " + user.get().getFirstname() + " " + user.get().getPatronymic());
            }
            userCourse.setProgress_percentage((checked_lessons * 100.0) / lessons_by_course);
            userCourseRepository.save(userCourse);
        }
        return ResponseEntity.ok("checked");
    }

    public ResponseEntity<?> getSubChapterCheck(Principal principal, int course_id){
        Optional<User> user = userRepository.findByEmail(principal.getName());
//        System.out.println("fefeefefe");
        List<UserLessonCheck> userLessonChecks = userLessonRepo.findCheckedByCourseId(user.get().getUser_id(),course_id);
        List<UserLessonCheckDTO> userLessonCheckDTOS = new ArrayList<>();
        for(UserLessonCheck userLessonCheck: userLessonChecks){
            UserLessonCheckDTO userLessonCheckDTO = new UserLessonCheckDTO();
            userLessonCheckDTO.setId(userLessonCheck.getLesson().getLesson_id());
            userLessonCheckDTO.setChecked(userLessonCheck.getChecked());
            userLessonCheckDTOS.add(userLessonCheckDTO);
        }
//        System.out.println(userLessonChecks);
        return ResponseEntity.ok(userLessonCheckDTOS);
    }

    public List<Chapter> getChapters(int course_id){
        return chapterRepo.findAllChapterByCourseId(course_id);
    }
    public List<Sub_chapter> getSub_chapters(int chapter_id){
        return subChapterRepo.findAllSub_ChapterByCourseId(chapter_id);
    }
    public List<Sub_chapter_materials> getSub_chapter_materials(int sub_chapter_id){
        return subChapterMaterialsRepo.findAllSub_chapter_materialsBySubChapterId(sub_chapter_id);
    }


}