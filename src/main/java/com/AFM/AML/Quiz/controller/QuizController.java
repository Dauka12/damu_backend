package com.AFM.AML.Quiz.controller;

import com.AFM.AML.Quiz.models.*;
import com.AFM.AML.Quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/aml/quiz")
public class QuizController {
    @Autowired
    QuizService quizService;


    @PostMapping("/saveQuiz")
    public ResponseEntity<?> saveQuiz(@RequestBody QuizData quizData) {
        return quizService.saveQuiz(quizData);
    }

    @PostMapping("/createQuiz/{module_id}")
    public String createQuiz(@RequestBody Quiz quiz, @PathVariable int module_id){
        String string = quizService.createQuiz(quiz,module_id);
        return string;
    }
    @PostMapping("/createQuestion/{quiz_idd}")
    public String createQuiz(@RequestBody Question question, @PathVariable int quiz_idd){
        String s = quizService.createQuestinForQuiz(question,quiz_idd);
        return s;
    }
    @PostMapping("/createQuestionMcqOptions/{quiz_idd}")
    public String createQuizQuestion(@RequestBody List<QuestionMcqRequest> questionMcqRequest,
                             @PathVariable int quiz_idd){
        String s = quizService.createQuestions_mcq(questionMcqRequest,quiz_idd);
        return s;
    }
    @GetMapping("/getQuiz/{module_id}")
    public Optional<Quiz> getQuiz(@PathVariable int module_id){
        return quizService.getQuiz(module_id);
    }
    @PostMapping("/checkQuiz/{quiz_id}")
    public String checkQuiz(@PathVariable int quiz_id, Principal principal, @RequestBody QuestionAnswerWrapper questionAnswerWrappers) {
        return quizService.submitQuiz(principal, quiz_id, questionAnswerWrappers.getMcqQuestionAnswerList(),questionAnswerWrappers.getMatchingPairAnswers());
    }
}
