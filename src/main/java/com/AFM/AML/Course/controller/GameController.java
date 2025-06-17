package com.AFM.AML.Course.controller;

import com.AFM.AML.Course.models.Chapter;
import com.AFM.AML.Course.models.DTOs.AnswersDTO;
import com.AFM.AML.Course.models.Sub_chapter;
import com.AFM.AML.Course.models.Sub_chapter_materials;
import com.AFM.AML.Course.models.game.*;
import com.AFM.AML.Course.repository.game.UserGameRepo;
import com.AFM.AML.Course.service.ChapterService;
import com.AFM.AML.Course.service.GameService;
import com.AFM.AML.Course.utils.ImageUtil;
import com.AFM.AML.User.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://192.168.122.132:3000", allowCredentials = "true")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

@RequestMapping("/api/aml/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;
    private final ImageUtil imageUtil;


    @PostMapping(value = "/createGame")
    public ResponseEntity<?> createGame(@RequestParam String name,@RequestParam String description,@RequestParam MultipartFile file,@RequestParam String duration){
        Game game = new Game();
        game.setName(name);
        game.setDescription(description);
        game.setDuration(duration);
        game.setIs_deleted(false);
        game.setIs_draft(true);
        game.setImage(imageUtil.fileToMinio(file));
        return ResponseEntity.ok(gameService.createGame(game));
    }

    @GetMapping(value = "/getAllGames")
    public ResponseEntity<?> getAllGames(){
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @GetMapping(value = "/getGameById/{id}")
    public ResponseEntity<?> getGameById(@PathVariable int id){
        return ResponseEntity.ok(gameService.getGameById(id));
    }

    @PutMapping(value = "/updateGame/{id}")
    public ResponseEntity<?> updateGame(@PathVariable Integer id,@RequestParam String name,@RequestParam String description,@RequestParam MultipartFile file,@RequestParam String duration){
        Game game = new Game();
        game.setName(name);
        game.setDescription(description);
        game.setDuration(duration);
        game.setIs_deleted(false);
        game.setIs_draft(true);
        game.setImage(imageUtil.fileToMinio(file));
        return ResponseEntity.ok(gameService.updateGame(id,game));
    }

    @PostMapping(value = "/addCategory/{id}")
    public ResponseEntity<?> addCategory(@PathVariable Integer id, @RequestParam String categoryName,
                                         @RequestParam String categoryDescription,@RequestParam MultipartFile file){
        Category category = new Category();
        category.setCategoryName(categoryName);
        category.setCategoryDescription(categoryDescription);
        category.setIs_draft(true);
        category.setIs_deleted(false);
        category.setCategoryImage(imageUtil.fileToMinio(file));
        return ResponseEntity.ok(gameService.addCategory(id,category));
    }

    @PostMapping(value = "/addLevel/{id}")
    public ResponseEntity<?> addLevel(@PathVariable Integer id,@RequestParam String level_name,
                                      @RequestParam String level_description){
        Level level = new Level();
        level.setLevel_name(level_name);
        level.setLevel_description(level_description);
        level.setIs_draft(true);
        level.setIs_deleted(false);
        return ResponseEntity.ok(gameService.addLevel(id,level));
    }

    @PostMapping(value = "/addSubLevel/{id}")
    public ResponseEntity<?> addSubLevel(@PathVariable Integer id,@RequestParam String subLevel_name){
        SubLevel subLevel=new SubLevel();
        subLevel.setSubLevel_name(subLevel_name);
        subLevel.setIs_draft(true);
        subLevel.setIs_deleted(false);
        return ResponseEntity.ok(gameService.addSubLevel(id,subLevel));
    }

    @PostMapping(value = "/addComponent/{id}")
    public ResponseEntity<?> addComponent(@PathVariable Integer id,@RequestBody Object component){
        return ResponseEntity.ok(gameService.addComponentEntry(id,component));
    }

    @PostMapping(value = "/refreshAnswers/{id}")
    public ResponseEntity<?> refreshAnswers(Principal principal,@PathVariable Integer id){
        return ResponseEntity.ok(gameService.refreshHistory(principal,id));
    }

    @PostMapping(value = "/addSurveyToGame/{id}")
    public ResponseEntity<?> addSurveyToGame(@PathVariable Integer id,@RequestBody Survey survey){
        return ResponseEntity.ok(gameService.addSurveyToGame(id,survey));
    }

    @PostMapping(value = "/submitSurvey")
    public ResponseEntity<?> submitSurvey(Principal principal,@RequestBody Survey survey){
        return ResponseEntity.ok(gameService.addSurveyToUserGame(principal,survey));
    }

    @PostMapping(value = "/setStatus/{id}")
    public ResponseEntity<?> setStatus(Principal principal,@PathVariable Integer id){
        return ResponseEntity.ok(gameService.chooseSubject(principal,id));
    }

    @PostMapping(value = "/setAvatar/{id}")
    public ResponseEntity<?> setAvatar(Principal principal,@PathVariable Integer id){
        return ResponseEntity.ok(gameService.chooseAvatar(principal,id));
    }

    @GetMapping(value = "/findSurveyById/{id}")
    public ResponseEntity<?> findSurveyById(@PathVariable Integer id){
        return ResponseEntity.ok(gameService.findSurveyById(id));
    }

    @PostMapping(value = "/addUserToGame/{user_id}/{game_id}")
    public ResponseEntity<?> addUserToGame(@PathVariable Integer user_id,@PathVariable Integer game_id){
        return ResponseEntity.ok(gameService.addUserToGame(user_id,game_id));
    }

    @PostMapping(value = "/checkAnswer")
    public ResponseEntity<?> checkAnswer(Principal principal,@RequestBody AnswersDTO answer){
        answer.setTaskId(answer.getTaskId()-1);
        answer.setLevelId(answer.getLevelId()-1);
        answer.setSubLevelId(answer.getSubLevelId()-1);
        return ResponseEntity.ok(gameService.checkAnswer(principal,answer));
    }

    @GetMapping(value = "/getResults")
    public ResponseEntity<?> getResults(Principal principal){
        return ResponseEntity.ok(gameService.getUserGameLevels(principal));
    }

    @GetMapping("/taskIsActive")
    public ResponseEntity<?> taskIsActive(Principal principal,@RequestBody AnswersDTO answersDTO){
        answersDTO.setTaskId(answersDTO.getTaskId()-1);
        answersDTO.setLevelId(answersDTO.getLevelId()-1);
        answersDTO.setSubLevelId(answersDTO.getSubLevelId()-1);
        return ResponseEntity.ok(gameService.taskIsActive(principal,answersDTO));
    }

    @GetMapping(value = "/getUserGame")
    public ResponseEntity<?> getUserGame(Principal principal){
        return ResponseEntity.ok(gameService.getUserGame(principal));
    }

    @GetMapping(value = "/getUserGameById/{id}")
    public ResponseEntity<?> getUserGame(@PathVariable Integer id){
        return ResponseEntity.ok(gameService.getUserGame(id));
    }

    @GetMapping(value = "/getAvatarInfo")
    public ResponseEntity<?> getAvatarInfo(Principal principal){
        return ResponseEntity.ok(gameService.getAvatarInfo(principal));
    }




//    @Autowired
//    GameService gameService;
//
//    @Autowired
//    UserGameRepo userGameRepo;
//
//    @GetMapping("/getUserGames")
//    public ResponseEntity<?> getUserGames(Principal principal){
//        List<UserGame> userGames = userGameRepo.findAll();
//        return ResponseEntity.ok(userGames);
//    }
//
//    @GetMapping("/getAllGames")
//    public ResponseEntity<?> getAllGames(){
//        List<UserGame> userGames =  gameService.getUserGames();
//        return ResponseEntity.ok(userGames);
//
//    }
//
//
//
//        //Lessons
////        @GetMapping(value = "/lessonsByModuleId")
////        public ResponseEntity<Object> getModulesLessons(@RequestParam int id) {
////            return gameService.getLessons(id);
////        }
//
//        @PostMapping(value = "/deleteLevel")
//        public ResponseEntity<Object> deleteLevel(@RequestParam int id) {
//            return gameService.deleteLevel(id);
//        }
//        @PostMapping(value ="/addLevel")
//        public ResponseEntity<Object> addLevel(@RequestBody Map<String, Object> requestBody) {
//            return gameService.addLevel(requestBody);
//        }
//
//        @GetMapping(value = "/getComponents")
//        public ResponseEntity<Object> getComponents(@RequestParam int id) {
//            return gameService.getComponents(id);
//        }
//
//        @PostMapping("/saveComponents/{id}")
//        public ResponseEntity<?> saveComponents(@RequestBody List<Object> componentHistory, @PathVariable int id) {
//            try {
//                gameService.saveComponents(componentHistory, id);
//                return ResponseEntity.ok().body("Components saved successfully.");
//            } catch (NotFoundException e) {
//                return ResponseEntity.notFound().build();
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body("Error saving components: " + e.getMessage());
//            }
//        }
//
//        @GetMapping("/getAllComponentEntriesPhoto")
//        public ResponseEntity<?> getAllComponentEntriesWithPhoto() throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//            return ResponseEntity.ok().body(gameService.getAllComponentEntries());
//        }
//
////        @PostMapping(value = "/createChapter/{course_id}")
////            public String saveChapter(@RequestParam String chapter,
////                    @RequestParam("file") MultipartFile file,
////            @PathVariable int course_id) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
////                ObjectMapper objectMapper = new ObjectMapper();
////                Chapter chapter1 = objectMapper.readValue(chapter, Chapter.class);
////                System.out.println(chapter1);
////                String objectName = UUID.randomUUID().toString();
////                Map<String,String> metadata = new HashMap<>();
////                metadata.put("originalFilename", file.getOriginalFilename());
////                minioClient.putObject(
////                        PutObjectArgs.builder()
////                                .bucket("aml")
////                                .object(objectName)
////                                .userMetadata(metadata)
////                                .stream(file.getInputStream(), file.getSize(), -1)
////                                .contentType(file.getContentType())
////                                .build()
////                );
////                chapter1.setChapter_image(objectName);
////                return chapterService.saveChapter(chapter1, course_id);
////        }
////        @PostMapping("/checked/{lesson_id}")
////        public ResponseEntity<?> checked(@PathVariable int lesson_id, Principal principal){
////            return chapterService.checkedSubChapter(principal,lesson_id);
////        }
////        @GetMapping("/getChecked/{course_id}")
////        public ResponseEntity<?> getChecked(Principal principal, @PathVariable int course_id){
////            return chapterService.getSubChapterCheck(principal, course_id);
////        }
////        @PostMapping(value = "/createSub_chapter/{chapter_id}")
////        public String saveSub_chapter(@RequestParam String sub_chapter,
////                                      @RequestParam("file")MultipartFile file,
////                                      @PathVariable int chapter_id) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
////            ObjectMapper objectMapper = new ObjectMapper();
////            Sub_chapter sub_chapter1 = objectMapper.readValue(sub_chapter, Sub_chapter.class);
////            System.out.println(sub_chapter1);
////            String objectName = UUID.randomUUID().toString();
////            Map<String,String> metadata = new HashMap<>();
////            metadata.put("originalFilename", file.getOriginalFilename());
////            minioClient.putObject(
////                    PutObjectArgs.builder()
////                            .bucket("aml")
////                            .object(objectName)
////                            .userMetadata(metadata)
////                            .stream(file.getInputStream(), file.getSize(), -1)
////                            .contentType(file.getContentType())
////                            .build()
////            );
////            sub_chapter1.setSub_chapter_image(objectName);
////            return chapterService.createSub_chapter(sub_chapter1, chapter_id);
////        }

    }


