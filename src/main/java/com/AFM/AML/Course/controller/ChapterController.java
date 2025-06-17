package com.AFM.AML.Course.controller;

import com.AFM.AML.Course.models.*;
import com.AFM.AML.Course.repository.ComponentEntryValuesRepo;
import com.AFM.AML.Course.service.ChapterService;
import com.AFM.AML.Course.utils.ImageUtil;
import com.AFM.AML.User.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/aml/chapter")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(origins = "http://192.168.122.132:3000", allowCredentials = "true")

public class ChapterController {
    @Autowired
    ChapterService chapterService;
    @Autowired
    private MinioClient minioClient;

    @Autowired
    private ComponentEntryValuesRepo componentEntryValuesRepo;


    @PostMapping(value = "/deleteModule")
    public ResponseEntity<Object> deleteModule(@RequestParam int id) {
        return chapterService.deleteModule(id);
    }
    @PostMapping(value ="/addModule")
    public ResponseEntity<Object> addModule(@RequestBody Map<String, Object> requestBody) {
        return chapterService.addModule(requestBody);
    }
    @GetMapping(value = "/modulesOfCourse")
    public ResponseEntity<Object> getModulesOfCourse(@RequestParam int id) {
        return chapterService.getModulesByCourseId(id);
    }

    //Lessons
    @GetMapping(value = "/lessonsByModuleId")
    public ResponseEntity<Object> getModulesLessons(@RequestParam int id) {
        return chapterService.getLessons(id);
    }

    @PostMapping(value = "/deleteLesson")
    public ResponseEntity<Object> deleteLesson(@RequestParam int id) {
        return chapterService.deleteLesson(id);
    }
    @PostMapping(value ="/addLesson")
    public ResponseEntity<Object> addLesson(@RequestBody Map<String, Object> requestBody) {
        return chapterService.addLesson(requestBody);
    }

    @GetMapping(value = "/getComponents")
    public ResponseEntity<Object> getComponents(@RequestParam int id) {
        return chapterService.getComponents(id);
    }

    @PostMapping("/saveComponents/{id}")
    public ResponseEntity<?> saveComponents(@RequestBody List<Object> componentHistory, @PathVariable int id) {
        try {
            chapterService.saveComponents(componentHistory, id);
            return ResponseEntity.ok().body("Components saved successfully.");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving components: " + e.getMessage());
        }
    }

    @GetMapping("/getAllComponentEntriesPhoto")
    public ResponseEntity<?> getAllComponentEntriesWithPhoto() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok().body(chapterService.getAllComponentEntries());
    }

    @PostMapping(value = "/createChapter/{course_id}")
    public String saveChapter(@RequestParam String chapter,
                              @RequestParam("file")MultipartFile file,
                              @PathVariable int course_id) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ObjectMapper objectMapper = new ObjectMapper();
        Chapter chapter1 = objectMapper.readValue(chapter, Chapter.class);
        System.out.println(chapter1);
        String objectName = UUID.randomUUID().toString();
        Map<String,String> metadata = new HashMap<>();
        metadata.put("originalFilename", file.getOriginalFilename());
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("aml")
                        .object(objectName)
                        .userMetadata(metadata)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        chapter1.setChapter_image(objectName);
        return chapterService.saveChapter(chapter1, course_id);
    }
    @PostMapping("/checked/{lesson_id}")
    public ResponseEntity<?> checked(@PathVariable int lesson_id, Principal principal){
        try {
            return chapterService.checkedSubChapter(principal,lesson_id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping("/getChecked/{course_id}")
    public ResponseEntity<?> getChecked(Principal principal, @PathVariable int course_id){
        return chapterService.getSubChapterCheck(principal, course_id);
    }
    @PostMapping(value = "/createSub_chapter/{chapter_id}")
    public String saveSub_chapter(@RequestParam String sub_chapter,
                              @RequestParam("file")MultipartFile file,
                              @PathVariable int chapter_id) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ObjectMapper objectMapper = new ObjectMapper();
        Sub_chapter sub_chapter1 = objectMapper.readValue(sub_chapter, Sub_chapter.class);
        System.out.println(sub_chapter1);
        String objectName = UUID.randomUUID().toString();
        Map<String,String> metadata = new HashMap<>();
        metadata.put("originalFilename", file.getOriginalFilename());
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("aml")
                        .object(objectName)
                        .userMetadata(metadata)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
        sub_chapter1.setSub_chapter_image(objectName);
        return chapterService.createSub_chapter(sub_chapter1, chapter_id);
    }
    @PostMapping(value = "/createSub_chapter_material/{chapter_id}")
    public String saveSub_chapter_materials(@RequestParam String sub_chapter_materials,
                              @RequestParam("file")MultipartFile file,
                              @PathVariable int sub_chapter_id) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ObjectMapper objectMapper = new ObjectMapper();
        Sub_chapter_materials sub_chapter_material = objectMapper.readValue(sub_chapter_materials, Sub_chapter_materials.class);
        String objectName = UUID.randomUUID().toString();
        Map<String,String> metadata = new HashMap<>();
        metadata.put("originalFilename", file.getOriginalFilename());
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("aml")
                        .object(objectName)
                        .userMetadata(metadata)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
//        sub_chapter_material.set(objectName);
//        return chapterService.createSub_chapter(sub_chapter1, chapter_id);
        return null;
    }
    @GetMapping("/getChapters/{course_id}")
    public List<Chapter> getChapters(@PathVariable int course_id){
        return chapterService.getChapters(course_id);
    }
    @GetMapping("/getSub_chapters/{chapter_id}")
    public List<Sub_chapter> getSub_chapters(@PathVariable int chapter_id){
        return chapterService.getSub_chapters(chapter_id);
    }
    @GetMapping("/getSub_chapter_materials/{sub_chapter_id}")
    public List<Sub_chapter_materials> getSub_chapter_materials(@PathVariable int sub_chapter_id){
        return chapterService.getSub_chapter_materials(sub_chapter_id);
    }
}
