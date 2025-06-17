package com.AFM.AML.Quiz.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerWrapper {
    private List<MCQ_QUESTION_ANSWER> mcqQuestionAnswerList;
    private List<MATCHING_PAIR_ANSWER> matchingPairAnswers;

}
