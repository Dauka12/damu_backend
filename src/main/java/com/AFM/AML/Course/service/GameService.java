package com.AFM.AML.Course.service;

import com.AFM.AML.Course.models.ComponentEntry;
import com.AFM.AML.Course.models.ComponentEntryValues;
import com.AFM.AML.Course.models.DTOs.AnswersDTO;
import com.AFM.AML.Course.models.Lesson;
import com.AFM.AML.Course.models.game.*;
import com.AFM.AML.Course.repository.game.*;
import com.AFM.AML.Course.utils.ImageUtil;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;


@Service
@RequiredArgsConstructor
public class GameService {
    private final UserRepository userRepository;
    private final UserGameRepo userGameRepo;
    private final GameRepo gameRepository;
    private final CategoryRepo categoryRepository;
    private final LevelRepo levelRepository;
    private final SubLevelRepo subLevelRepository;
    private final ComponentEntryGameRepo componentEntryGameRepo;
    private final ComponentEntryValuesGameRepo componentEntryValuesGameRepo;

    private final SurveyRepo surveyRepo;
    private final SurveyQuestionRepo surveyQuestionRepo;
    private final SurveyAnswerRepo surveyAnswerRepo;

    private final UserGameLevelRepo userGameLevel;
    private final UserGameSubLevelRepo userGameSubLevel;

    private final ImageUtil imageUtil;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final UserGameSubLevelRepo userGameSubLevelRepo;
    private final UserGameLevelRepo userGameLevelRepo;

    private final AvatarInfoRepo avatarInfoRepo;

    public Game createGame(Game game){
        return gameRepository.save(game);
    }

    public Game updateGame(Integer id, Game game){
        Game game1=gameRepository.findById(id).get();
        game1.setName(game.getName());
        game1.setDescription(game.getDescription());
        game1.setImage(game.getImage());
        game1.setIs_deleted(game.getIs_deleted());
        game1.setIs_draft(game.getIs_draft());
        game1.setDuration(game.getDuration());
        return gameRepository.save(game1);
    }

    public List<Game> getAllGames(){
        return gameRepository.findAll();
    }

    public Game getGameById(Integer id){
        return gameRepository.findById(id).get();
    }

    public Game addCategory(Integer id, Category category){
        Game game=gameRepository.findById(id).get();
        category.setGameId(game);
        categoryRepository.save(category);
        return game;
    }

    public Boolean taskIsActive(Principal principal,AnswersDTO answersDTO){
        User user=userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserGame userGame=userGameRepo.findUserGameByUser(user)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return 0 >= userGame.getUserGameLevel().get(answersDTO.getLevelId()).getUserGameSubLevelList().get(answersDTO.getSubLevelId()).getAnswers().get(answersDTO.getTaskId());

    }

    public Category addLevel(Integer id, Level level){
        Category category=categoryRepository.findById(id).get();
        level.setCategory(category);
        levelRepository.save(level);
        return category;
    }

    public Level addSubLevel(Integer id, SubLevel subLevel){
        Level level=levelRepository.findById(id).get();
        subLevel.setLevel(level);
        subLevelRepository.save(subLevel);
        return level;
    }

    @Transactional
    public SubLevel addComponentEntry(Integer id, Object componentEntry) {
        try {
            SubLevel subLevel = subLevelRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid SubLevel ID"));
            ComponentEntryGame componentEntryGame = convertToComponentEntry(componentEntry, subLevel);

            // Save ComponentEntryValuesGame first
//            componentEntryGameVaRepo.save(componentEntryGame.getValues());
            // Then save ComponentEntryGame
            componentEntryGameRepo.save(componentEntryGame);


            return subLevel;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Survey addSurveyToGame(Integer gameId, Survey survey) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        for (SurveyQuestion question : survey.getQuestionList()) {
            for (SurveyAnswer answer : question.getAnswersList()) {
                answer.setSurveyQuestion(question);
                surveyAnswerRepo.save(answer);
            }
            question.setSurvey(survey);
            surveyQuestionRepo.save(question);
        }
        survey.setGame_id(game);
        return surveyRepo.save(survey);
    }


    public UserGame chooseSubject(Principal principal, Integer id){
        UserGame userGame = userGameRepo.findUserGameByUser(userRepository.findByEmail(principal.getName()).get())
                .orElseThrow(() -> new RuntimeException("UserGame not found"));
        userGame.setStatus(id);
        return userGameRepo.save(userGame);
    }



    public UserGame chooseAvatar(Principal principal, Integer id){
        UserGame userGame = userGameRepo.findUserGameByUser(userRepository.findByEmail(principal.getName()).get())
                .orElseThrow(() -> new RuntimeException("UserGame not found"));
        userGame.setAvatar_id(id);
        return userGameRepo.save(userGame);
    }

    public Survey addSurveyToUserGame(Principal principal, Survey survey) {
        UserGame userGame = userGameRepo.findUserGameByUser(userRepository.findByEmail(principal.getName()).get())
                .orElseThrow(() -> new RuntimeException("UserGame not found"));

        // Find and delete the old survey, if it exists
        Optional<Survey> oldSurvey = surveyRepo.findSurveyByUserGame(userGame);
        if (oldSurvey.isPresent()) {
            for (SurveyQuestion oldQuestion : oldSurvey.get().getQuestionList()) {
                surveyAnswerRepo.deleteAll(oldQuestion.getAnswersList());
            }
            surveyQuestionRepo.deleteAll(oldSurvey.get().getQuestionList());
            surveyRepo.deleteById(oldSurvey.get().getSurvey_id());
        }

        // Save the survey first so that it has an ID
        survey.setUserGame(userGame);
        surveyRepo.save(survey);

        // Then save each question and answer, associating them with the saved survey
        for (SurveyQuestion question : survey.getQuestionList()) {
            question.setSurvey(survey); // Now the survey is saved, so this reference is valid
            surveyQuestionRepo.save(question); // Save the question to get its ID

            for (SurveyAnswer answer : question.getAnswersList()) {
                answer.setSurveyQuestion(question); // Now the question is saved, so this reference is valid
                surveyAnswerRepo.save(answer);
            }
        }

        return survey;
    }




    @Transactional
    public UserGame addUserToGame(Integer user_id, Integer game_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Game game = gameRepository.findById(game_id)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Optional<UserGame> userGameOptional = userGameRepo.findUserGameByUser(user);
        if (userGameOptional.isPresent()) {
            UserGame existingUserGame = userGameOptional.get();
            if (existingUserGame.getSurvey() != null) {
                Survey oldSurvey = existingUserGame.getSurvey();
                for (SurveyQuestion oldQuestion : oldSurvey.getQuestionList()) {
                    surveyAnswerRepo.deleteAll(oldQuestion.getAnswersList());
                }
                surveyQuestionRepo.deleteAll(oldSurvey.getQuestionList());
                surveyRepo.deleteById(oldSurvey.getSurvey_id());
            }
            if(existingUserGame.getUserGameLevel()!=null){
                deleteAllUserGameLevels(existingUserGame.getUserGameLevel());
            }
            userGameRepo.deleteById(existingUserGame.getId());
        }

        UserGame userGame = new UserGame();
        userGame.setUser(user);
        userGame.setGame(game);
        userGame.setUserGameLevel(new ArrayList<>());
        userGame.setStatus(0);
        userGame.setPercentage(0.0);
        userGame.setAvatar_id(1);
        // Save UserGame first to ensure it has an ID
        userGame = userGameRepo.save(userGame);

        createAndSaveUserGameLevels(userGame);


        return userGame;
    }

    private void deleteAllUserGameLevels(List<UserGameLevel> userGameLevels) {
        if(userGameLevels.isEmpty()){
            return;
        }
        for (UserGameLevel gameLevel : userGameLevels) {
            userGameSubLevelRepo.deleteAll(gameLevel.getUserGameSubLevelList());
        }
        userGameLevelRepo.deleteAll(userGameLevels);
    }

    @Transactional
    public UserGame refreshHistory(Principal principal,Integer i){
        i--;
        User user=userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserGame userGame=userGameRepo.findUserGameByUser(user)
                .orElseThrow(() -> new RuntimeException("UserGame not found"));

            for(int j=0;j<userGame.getUserGameLevel().get(i).getUserGameSubLevelList().size();j++){
                userGame.getUserGameLevel().get(i).getUserGameSubLevelList().get(j).setPercentage(0.0);
                for(int k=0;k<userGame.getUserGameLevel().get(i).getUserGameSubLevelList().get(j).getAnswers().size();k++){
                    userGame.getUserGameLevel().get(i).getUserGameSubLevelList().get(j).getAnswers().set(k,0.0);
                }
            }
            userGame.getUserGameLevel().get(i).setIsPassed(false);
            userGame.getUserGameLevel().get(i).setIsActive(false);
            userGame.getUserGameLevel().get(i).setPercentage(0.0);

        userGame.getUserGameLevel().get(0).setIsActive(true);
        userGame.setPercentage(0.0);
        userGameRepo.save(userGame);
        return userGame;
    }

    private void createAndSaveUserGameLevels(UserGame userGame) {
        UserGameLevel userGameLevel1 = new UserGameLevel();
        userGameLevel1.setIsActive(true);
        userGameLevel1.setUserGame(userGame);
        userGameLevel1 = userGameLevelRepo.save(userGameLevel1);  // Save the first level
        addingAnswers(1, userGameLevel1);
        addingAnswers(2, userGameLevel1);
        addingAnswers(4, userGameLevel1);
        addingAnswers(2, userGameLevel1);
        addingAnswers(1, userGameLevel1);

        UserGameLevel userGameLevel2 = new UserGameLevel();
        userGameLevel2.setUserGame(userGame);
        userGameLevel2 = userGameLevelRepo.save(userGameLevel2);  // Save the second level
        addingAnswers(4, userGameLevel2);
        addingAnswers(3, userGameLevel2);
        addingAnswers(4, userGameLevel2);
        addingAnswers(5, userGameLevel2);

        UserGameLevel userGameLevel3 = new UserGameLevel();
        userGameLevel3.setUserGame(userGame);
        userGameLevel3 = userGameLevelRepo.save(userGameLevel3);  // Save the third level
        addingAnswers(1, userGameLevel3);

        UserGameLevel userGameLevel4 = new UserGameLevel();
        userGameLevel4.setUserGame(userGame);
        userGameLevel4 = userGameLevelRepo.save(userGameLevel4);  // Save the fourth level
        addingAnswers(1, userGameLevel4);
    }


    public UserGame checkAnswer(Principal principal, AnswersDTO answersDTO){
        User user=userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserGame userGame=userGameRepo.findUserGameByUser(user)
                .orElseThrow(() -> new RuntimeException("User not found"));
        assert userGame.getUserGameLevel() != null;
        List<UserGameLevel> levels=userGameLevelRepo.findAllByUserGame(userGame);
        UserGameLevel userGameLevel1=userGame.getUserGameLevel().get(answersDTO.getLevelId());
        userGameLevel1.getUserGameSubLevelList().get(answersDTO.getSubLevelId()).getAnswers().set(answersDTO.getTaskId(),answersDTO.getIsCorrect());
        Double total=0.0;
        Double onePercenge=(double)100/userGameLevel1.getUserGameSubLevelList().get(answersDTO.getSubLevelId()).getAnswers().size();
        for(int i=0;i<userGameLevel1.getUserGameSubLevelList().get(answersDTO.getSubLevelId()).getAnswers().size();i++){
            total+=userGameLevel1.getUserGameSubLevelList().get(answersDTO.getSubLevelId()).getAnswers().get(i)*onePercenge;
        }
        userGameLevel1.getUserGameSubLevelList().get(answersDTO.getSubLevelId()).setPercentage(total);
//        userGameSubLevelRepo.save(userGameLevel1.getUserGameSubLevelList().get(answersDTO.getSubLevelId()));

        double newtotal=0.0;
        Double newonePercenge=(double)100/userGameLevel1.getUserGameSubLevelList().size();
//        System.out.println(newonePercenge);
        for(int i=0;i<userGameLevel1.getUserGameSubLevelList().size();i++){
            newtotal+=(newonePercenge*userGameLevel1.getUserGameSubLevelList().get(i).getPercentage()/100);

        }
        userGameLevel1.setPercentage(newtotal);
        if(newtotal>70){
            userGameLevel1.setIsPassed(true);
            if(userGame.getUserGameLevel().size()-1!=answersDTO.getLevelId()){
                userGame.getUserGameLevel().get(answersDTO.getLevelId()+1).setIsActive(true);
            }
        }

        if(answersDTO.getTaskId()==0&&answersDTO.getSubLevelId()==4&&answersDTO.getLevelId()==1){
            AnswersDTO ans=new AnswersDTO();
            ans.setTaskId(1);
            ans.setSubLevelId(4);
            ans.setLevelId(1);
            ans.setIsCorrect(1.0);
            checkAnswer(principal,ans);
        }
        return userGameRepo.save(userGame);
    }

    private UserGameSubLevel addingAnswers(Integer n,UserGameLevel userGameLevel){
        UserGameSubLevel userGameSubLevel=new UserGameSubLevel();
        userGameSubLevel.setQuantity(n);
        List<Double> ls=new ArrayList<>();
        for(int i=0;i<n;i++){
            ls.add((double)0);
        }
        userGameSubLevel.setAnswers(ls);
        userGameSubLevel.setUserGameLevel(userGameLevel);
        return userGameSubLevelRepo.save(userGameSubLevel);
    }



    public Survey findSurveyById(Integer id){
        return surveyRepo.findById(id).get();
    }

    public List<UserGameLevel> getUserGameLevels(Principal principal) {
        User user=userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserGame userGame=userGameRepo.findUserGameByUser(user)
                .orElseThrow(() -> new RuntimeException("UserGame not found"));
        return userGame.getUserGameLevel();

    }

    public AvatarInfo getAvatarInfo(Principal principal){
        User user=userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserGame userGame=userGameRepo.findUserGameByUser(user)
                .orElseThrow(() -> new RuntimeException("UserGame not found"));
        return avatarInfoRepo.findById(userGame.getAvatar_id())
                .orElseThrow(() -> new RuntimeException("Avatar not found"));
    }

    public UserGame getUserGame(Principal principal) {
        User user=userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserGame userGame=userGameRepo.findUserGameByUser(user)
                .orElseThrow(() -> new RuntimeException("UserGame not found"));
        int count=0;
        for(int i=0;i<userGame.getUserGameLevel().size();i++){
            if(userGame.getUserGameLevel().get(i).getIsPassed()){
                count++;
            }
        }
        userGame.setPercentage((double)count/userGame.getUserGameLevel().size()*100);
        userGameRepo.save(userGame);
        return userGame;
    }

    public UserGame getUserGame(Integer id) {
        User user=userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserGame userGame=userGameRepo.findUserGameByUser(user)
                .orElseThrow(() -> new RuntimeException("UserGame not found"));
        int count=0;
        for(int i=0;i<userGame.getUserGameLevel().size();i++){
            if(userGame.getUserGameLevel().get(i).getIsPassed()){
                count++;
            }
        }
        userGame.setPercentage((double)count/userGame.getUserGameLevel().size()*100);
        userGameRepo.save(userGame);
        return userGame;
    }


    private ComponentEntryGame convertToComponentEntry(Object component, SubLevel subLevel) throws Exception {
        String json = objectMapper.writeValueAsString(component);
        Map<String, Object> componentMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});

        ComponentEntryGame componentEntry = new ComponentEntryGame();
        componentEntry.setComponentName((String) componentMap.get("componentName"));
        componentEntry.setSublevel(subLevel);

        Map<String, Object> values = (Map<String, Object>) componentMap.get("values");
        ComponentEntryValuesGame componentEntryValues = new ComponentEntryValuesGame();
        Map<String, String> parsedValues = new HashMap<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            parsedValues.put(entry.getKey(), String.valueOf(entry.getValue()));
        }
        componentEntryValues.setValues(parsedValues);
        componentEntry.setValues(componentEntryValues);


        return componentEntry;
    }



//    @Autowired
//    UserGameRepo userGameRepo;
//
//        @Autowired
//        UserRepository userRepository;
//        @Autowired
//        Sub_chapterRepo subChapterRepo;
//        @Autowired
//        Sub_chapter_materials_repo subChapterMaterialsRepo;
//        @Autowired
//        ComponentEntryGameRepo componentEntryGameRepo;
//
//        @Autowired
//        LevelRepo levelRepo;
//        @Autowired
//        UserCourseRepository userCourseRepository;
//        @Autowired
//        Quiz_resultsRepository quizResultsRepository;
//        @Autowired
//        ComponentEntryValuesGameRepo componentEntryValuesGameRepo;
//        @Autowired
//        MinioService minioService;
//
//        @Autowired
//        GameRepo gameRepo;
//        private static final ObjectMapper objectMapper = new ObjectMapper();

//    @Autowired
//    UserChapterCheckDtoRepo userChapterCheckDtoRepo;

//    public List<UserGame> getUserGames(){
//        return userGameRepo.findAll();
//    }
//        public ResponseEntity<?> getAllComponentEntries() throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//            List<ComponentEntryValuesGame> componentEntryValues = componentEntryValuesGameRepo.getComponentEntriesByPhoto();
//            for(ComponentEntryValuesGame componentEntryValues1 : componentEntryValues) {
//                String base64String = componentEntryValues1.getValues().get("img");
//                String[] parts = base64String.split(",");
//                String contentType = parts[0].split(";")[0].split(":")[1];
//                String base64Data = parts[1];
//                byte[] decodedBytes = DatatypeConverter.parseBase64Binary(base64Data);
//                MultipartFile file = new MockMultipartFile("file", "filename", contentType, new ByteArrayInputStream(decodedBytes));
//                String fileName = UUID.randomUUID().toString();
//                String path = fileName;
//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append("fdsf");
//                minioService.uploadFile(file, path);
//                componentEntryValues1.getValues().put("img", "http://192.168.122.132/aml/" + path);
//                componentEntryValuesGameRepo.save(componentEntryValues1);
//            }
//            return ResponseEntity.ok().body("d");
//        }
//
//        public MultipartFile convert(String base64String) throws IOException {
//            // Split the base64 string into its data and metadata parts
//            String[] parts = base64String.split(",");
//
//            // Extract the content type and base64 data
//            String contentType = parts[0].split(";")[0].split(":")[1];
//            String base64Data = parts[1];
//
//            // Decode the base64 data
//            byte[] decodedBytes = DatatypeConverter.parseBase64Binary(base64Data);
//
//            // Create a MultipartFile instance
//            return new MockMultipartFile("file", "filename", contentType, new ByteArrayInputStream(decodedBytes));
//        }
//
//        public ResponseEntity<Object> getComponents(int id) {
//            Optional<Level> level = levelRepo.findById(id);
//            if (level.isPresent()) {
//                List<ComponentEntryGame> componentEntries = componentEntryGameRepo.findByLessonId(id);
//                return ResponseEntity.ok(componentEntries);
//            }
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("GivenLesson was not found");
//        }
//        @Transactional
//        public void saveComponents(List<Object> componentHistory, int id) throws Exception {
//            Optional<Level> levelOptional = levelRepo.findById(id);
//            if (!levelOptional.isPresent()) {
//                throw new NotFoundException("Lesson with id " + id + " was not found.");
//            }
//
//            Level level = levelOptional.get();
//            List<ComponentEntryGame> existingEntries = componentEntryGameRepo.findByLessonId(id);
//            componentEntryGameRepo.deleteAll(existingEntries);
//
//            for (Object component : componentHistory) {
//                ComponentEntryGame componentEntryGame = convertToComponentEntry(component, level);
//                componentEntryGameRepo.save(componentEntryGame);
//            }
//        }
//
//        private ComponentEntryGame convertToComponentEntry(Object component, Level level) throws Exception {
//            String json = objectMapper.writeValueAsString(component);
//            Map<String, Object> componentMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
//
//            ComponentEntryGame componentEntryGame = new ComponentEntryGame();
//            componentEntryGame.setComponentName((String) componentMap.get("componentName"));
//            componentEntryGame.setLevel(level);
//
//            Map<String, Object> values = (Map<String, Object>) componentMap.get("values");
//            ComponentEntryValues componentEntryValues = new ComponentEntryValues();
//            Map<String, String> parsedValues = new HashMap<>();
//            for (Map.Entry<String, Object> entry : values.entrySet()) {
//                parsedValues.put(entry.getKey(), String.valueOf(entry.getValue()));
//            }
//            componentEntryValues.setValues(parsedValues);
//            componentEntryGame.setValues(componentEntryValues);
//
//            return componentEntryGame;
//        }
////        public ResponseEntity<Object> getModulesByCourseId(int id) {
////            try {
////                List<Module> modules = moduleRepo.findByCourseIdAndActive(id);
////                return ResponseEntity.ok(modules);
////            } catch (Exception e) {
////                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
////            }
////        }
//
//        //Lessons aka Sub_chapters
//        public ResponseEntity<Object> deleteLevel(int id) {
//            try {
//                Optional<Level> optionalLevel = levelRepo.findById(id);
//
//                if (optionalLevel.isPresent()) {
//                    Level level = optionalLevel.get();
////                    level.set_active(false);
//
//                    levelRepo.save(level);
//
//                    List<Level> levels = levelRepo.findByGameIdAndActive(level.getGame().getGame_id());
//                    return ResponseEntity.ok(levels);
//                } else {
//                    return ResponseEntity.notFound().build();
//                }
//            } catch (Exception e) {
//                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//        public ResponseEntity<Object> addLevel(Map<String, Object> body) {
//            try {
//                int id = Integer.parseInt(body.get("id").toString());
//                String newLessonName = body.get("newLevelName").toString();
//                Optional<Game> optionalGame = gameRepo.findById(id);
//                if (optionalGame.isPresent()) {
//                    Level level  = new Level();
//                    level.setDescription(newLessonName);
//                    level.setGame(optionalGame.get());
//                    levelRepo.save(level);
//                    return ResponseEntity.ok(levelRepo.findByGameIdAndActive(id));
//                } else {
//                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
//                }
//            } catch (NumberFormatException | NullPointerException e) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//            }
//        }
//
////        public ResponseEntity<Object> getLessons(int id) {
////            try {
////                Optional<Game> game = gameRepo.findById(id);
////
////                    return ResponseEntity.ok(module);
////                } else {
////                    return ResponseEntity.notFound().build();
////                }
////            } catch (Exception e) {
////                // Log the exception for debugging
////                System.out.println("Error while retrieving lessons" + e);
////                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
////            }
////        }
 }

