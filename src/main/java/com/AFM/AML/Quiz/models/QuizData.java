package com.AFM.AML.Quiz.models;

import java.util.List;

public class QuizData {
    private int module_id;
    private Quiz quiz;
    private List<QuestionMcqRequest> questions;

    public int getModule_id() {
        return module_id;
    }

    public void setModule_id(int module_id) {
        this.module_id = module_id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<QuestionMcqRequest> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionMcqRequest> questions) {
        this.questions = questions;
    }
}
