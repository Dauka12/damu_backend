package com.AFM.AML.Quiz.service;

import com.AFM.AML.Course.models.*;
import com.AFM.AML.Course.models.Module;
import com.AFM.AML.Course.repository.*;
import com.AFM.AML.Quiz.repository.*;
import com.AFM.AML.User.models.Log;
import com.AFM.AML.User.models.User;
import com.AFM.AML.Quiz.models.*;
import com.AFM.AML.User.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    @Autowired
    Mcq_optionRepository mcqOptionRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    ModuleRepo moduleRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    Quiz_resultsRepository quizResultsRepository;
    @Autowired
    MatchingPairRepo matchingPairRepo;
    @Autowired
    CourseRepo courseRepo;
    @Autowired
    UserCourseRepository userCourseRepository;
    @Autowired
    UserLessonRepo userLessonRepo;

    public ResponseEntity<?> saveQuiz(QuizData quizData) {
        int moduleId = quizData.getModule_id();
        Quiz quiz = quizData.getQuiz();
        List<QuestionMcqRequest> questions = quizData.getQuestions();

        Optional<Module> module = moduleRepo.findById(moduleId);

        if (module.isPresent()) {
            Optional<Quiz> oldQuiz = quizRepository.findQuizByChapterID(moduleId);
            Quiz currentQuiz;

            if (oldQuiz.isPresent()) {
                // Update existing quiz
                currentQuiz = oldQuiz.get();
                currentQuiz.setQuiz_title(quiz.getQuiz_title());

                // Delete existing questions
                questionRepository.deleteByQuizId(currentQuiz.getQuiz_id());

                // Save updated quiz
                quizRepository.save(currentQuiz);
            } else {
                // Create new quiz
                quiz.setModule(module.get());
                currentQuiz = quizRepository.save(quiz);
            }

            // Create questions for the quiz
            createQuestions_mcq(questions, currentQuiz.getQuiz_id());
        }
        else {
            ResponseEntity.notFound();
        }
        // Return appropriate response entity
        return ResponseEntity.ok().body("Quiz saved successfully");
    }



    public String createQuiz(Quiz quiz, int module_id){
        Optional<Module> module = moduleRepo.findById(module_id);
        quiz.setModule(module.get());
        quizRepository.save(quiz);
        return "Quiz " + quiz + " saved";
    }
    public String createQuestinForQuiz(Question question, int quiz_idd){
        Optional<Quiz> quiz = quizRepository.findById(quiz_idd);
        question.setQuiz(quiz.get());
        questionRepository.save(question);
        return "Question " + question + " saved";
    }
    public String createQuestions_mcq(List<QuestionMcqRequest> questionMcqRequest,int quiz_id){
        Optional<Quiz> quiz = quizRepository.findById(quiz_id);
        for(QuestionMcqRequest question : questionMcqRequest) {
            Question question1 = question.getQuestion();
            question1.setQuiz(quiz.get());
            List<Mcq_option> mcqOption = question.getMcqOptionList();
            List<MatchingPair> matchingPairs = question.getMatchingPairs();
            System.out.println(mcqOption);
            questionRepository.save(question1);
            if(mcqOption != null){
            for (Mcq_option mcqOption1 : mcqOption) {
                mcqOption1.setQuestion(question1);
                mcqOptionRepository.save(mcqOption1);
                }
            }
            if(matchingPairs != null) {
                for (MatchingPair matchingPair : matchingPairs) {
                    matchingPair.setQuestion(question1);
                    matchingPairRepo.save(matchingPair);
                }
            }
        }
        return "Question and mcq_options " + questionMcqRequest + " saved";
    }

    public Optional<Quiz> getQuiz(int module_id){
        Optional<Quiz> quiz = quizRepository.findQuizByChapterID(module_id);
        System.out.println(quiz);
        return quiz;
    }

    public String submitQuiz(Principal principal,int quiz_id, List<MCQ_QUESTION_ANSWER> mcqQuestionAnswers,List<MATCHING_PAIR_ANSWER> matchingPairAnswers){

        Optional<User> user = userRepository.findByEmail(principal.getName());
        Optional<Quiz> quiz = quizRepository.findById(quiz_id);
        if(quizResultsRepository.checkIsChecksAccept(user.get().getUser_id(),quiz_id) == true){
            return "quiz completed";
        }
        if(quizResultsRepository.checkIsCheck(user.get().getUser_id(),quiz_id) != true) {
            Quiz_results quizResults = new Quiz_results();
            Log log = new Log();
            log.setActivity("quiz submit");
            Date date = new Date();
            log.setDate(date);
            double points = 0.0;
            double all_points = 0.0;
            
            all_points = questionRepository.findCountOfQuestions(quiz_id);
            
            System.out.println(all_points);
            if (mcqQuestionAnswers != null) {
                for (MCQ_QUESTION_ANSWER mcqQuestionAnswer : mcqQuestionAnswers) {
                    if (mcqOptionRepository.checkCorrectAnswer(mcqQuestionAnswer.getQuestion(), mcqQuestionAnswer.getAnswer())) {
                        points += 1.0;
                    }
                }
            }
            if (matchingPairAnswers != null) {
                for (MATCHING_PAIR_ANSWER matchingPairAnswer : matchingPairAnswers) {
                    if (matchingPairRepo.checkCorrectAnswerMatchingPair(matchingPairAnswer.getQuestion(), matchingPairAnswer.getLeft_part(), matchingPairAnswer.getRight_part())) {
                        points += (1.0 / matchingPairAnswers.size());
                        System.out.println(matchingPairAnswer.getLeft_part() + " " + matchingPairAnswer.getRight_part());
                    }
                }
            }
            double score = (100.0 * points) / all_points;
            System.out.println(score);
            System.out.println(points);
            log.setDescription(principal.getName() + " submitted quiz" + quiz + " quiz with " + score + " points");
            quizResults.setUser(user.get());
            quizResults.setQuiz(quiz.get());
            quizResults.setPoints(points);
            quizResults.setAll_points(all_points);
            quizResults.setScore(score);
            quizResultsRepository.save(quizResults);
            System.out.println(quiz.get().getModule().getCourse().getCourse_id() + " ///////////////////////////////");
            Course course = quiz.get().getModule().getCourse();
            UserCourse userCourse = userCourseRepository.findByUserAndCourseId(user.get().getUser_id(), course.getCourse_id());
            int lessons_by_course = 0;
            int checked_lessons = 0;
            if(course.getModules() != null){
                List<Module> module = course.getModules();
                for(Module module1 : module) {
                    if (module1.is_active() == true) {
                        System.out.println(module1.getModule_id());
                        checked_lessons += userLessonRepo.countOfCheckedLessons(user.get().getUser_id(), module1.getModule_id());
//                    System.out.println(module1.getModule_id());
//                    System.out.println(userChapterCheckRepo.getUserLessonCheckByModuleId(module1.getModule_id()));
                        for (Lesson lesson : module1.getLessons()) {
                            if (lesson.is_active()) {
                                lessons_by_course++;
                            }
                        }                        if (module1.getQuiz() != null) {
                            lessons_by_course += 1;
                            checked_lessons += quizResultsRepository.countOfFinishedTestsofUser(user.get().getUser_id(), module1.getQuiz().getQuiz_id());
                        }
                    }
                }
                
                // Обновляем прогресс курса
                double progressPercentage = (checked_lessons * 100.0) / lessons_by_course;
                userCourse.setProgress_percentage(progressPercentage);
                
                // Сохраняем прогресс только если квиз пройден успешно
                if(score >= 70) {
                    // Проверяем, завершен ли курс полностью
                    if(progressPercentage >= 100.0){
                        userCourse.setStatus("finished");
                        userCourse.setProgress_percentage(100.0);
                        
                        // Выдаем сертификат, если его еще нет
                        if(userCourse.getCertificate_int() == null || userCourse.getCertificate_int() == 0) {
                            UniqueIntegerGenerator integerGenerator = new UniqueIntegerGenerator();
                            int certificateNumber = integerGenerator.getNextUniqueInteger();
                            userCourse.setCertificate_int(certificateNumber);
                            
                            LocalDate currentLocalDate = LocalDate.now();
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            String formattedDate = currentLocalDate.format(dateFormatter);
                            userCourse.setDate_certificate(formattedDate);
                            userCourse.setStatic_full_name(user.get().getLastname() + " " + user.get().getFirstname() + " " + user.get().getPatronymic());
                        }
                    }
                    userCourseRepository.save(userCourse);
                }
            }
        }
        
        // Проверяем финальный статус квиза
        if(quizResultsRepository.checkIsChecksAccept(user.get().getUser_id(), quiz_id)) {
            return "quiz completed";
        }
        
        // Проверяем, не превышено ли количество попыток (больше 30 неудачных попыток)
        if(!quizResultsRepository.checkIsChecksAcceptNot(user.get().getUser_id(), quiz_id)) {
            return "quiz failed";
        } else {
            // Пользователь превысил лимит попыток - сбрасываем весь курс
            System.out.println("Resetting course: " + quiz.get().getModule().getCourse().getCourse_id());
            quizResultsRepository.deleteAllByQuizIdAndUserID(user.get().getUser_id(), quiz.get().getModule().getCourse().getCourse_id());
            userLessonRepo.unchecked(quiz.get().getModule().getCourse().getCourse_id(), user.get().getUser_id());
            userCourseRepository.resetCourse(user.get().getUser_id(), quiz.get().getModule().getCourse().getCourse_id());
            return "zanova";
        }
    }

}
