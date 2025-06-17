//package com.AFM.AML.scheduled;
//
//import com.AFM.AML.Course.models.Chapter;
//import com.AFM.AML.Course.models.Course;
//import com.AFM.AML.Course.models.CourseCategory;
//import com.AFM.AML.Course.models.Webinar;
//import com.AFM.AML.Course.repository.ChapterRepo;
//import com.AFM.AML.Course.repository.CourseCategoryRepo;
//import com.AFM.AML.Course.repository.CourseRepo;
//import com.AFM.AML.Course.repository.WebinarRepo;
//import com.AFM.AML.Minio.service.MinioService;
//import com.AFM.AML.Quiz.models.Question;
//import com.AFM.AML.Quiz.repository.QuestionRepository;
//import io.minio.errors.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.time.DayOfWeek;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//public class WeekendMinioProcessor {
//    @Autowired
//    ChapterRepo chapterRepo;
//    @Autowired
//    MinioService minioService;
//    @Autowired
//    CourseRepo courseRepo;
//    @Autowired
//    CourseCategoryRepo courseCategoryRepo;
//    @Autowired
//    QuestionRepository questionRepository;
//    @Autowired
//    WebinarRepo webinarRepo;
//
////    @Scheduled(cron = "0 56 14 ? * MON")
////    public void processUpdate(){.
////        webinarRepo.webinarUpdateIsActive();
////    }
//
//    @Scheduled(cron = "0 23 14 ? * WED")
//    public void processMinio() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidExpiresRangeException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        try {
//            LocalDateTime now = LocalDateTime.now();
//            DayOfWeek dayOfWeek = now.getDayOfWeek();
//            List<Webinar> webinars = webinarRepo.findAll();
//            for(Webinar webinar : webinars){
//                System.out.println(webinar.getLink());
//                webinarRepo.updateImage(minioService.getFileUrl(webinar.getMinio_image()),webinar.getWebinar_id());
//            }
//            List<Course> courseList = courseRepo.findAll();
//            for (Course course : courseList) {
//                course.setCourse_image(minioService.getFileUrl(course.getMinio_image_name()));
//                courseRepo.save(course);
////            System.out.println(chapter.getCourse_id());
//            }
//            List<CourseCategory> courseCategories = courseCategoryRepo.findAll();
//            for (CourseCategory courseCategory : courseCategories) {
//                courseCategory.setCategory_image(minioService.getFileUrl(courseCategory.getMinio_image_name()));
//                courseCategoryRepo.save(courseCategory);
//            }
//            System.out.println("1fefefefefefef");
//
//            List<Question> questions = questionRepository.findByMinioImage();
//            for (Question question : questions) {
//                question.setImage(minioService.getFileUrl(question.getMinio_image()));
//                questionRepository.save(question);
//            }
//            System.out.println("2fefefefefefef");
//
//
//            System.out.println("3fefefefefefef");
//        } catch (Exception e) {
//        }
//    }
//}
