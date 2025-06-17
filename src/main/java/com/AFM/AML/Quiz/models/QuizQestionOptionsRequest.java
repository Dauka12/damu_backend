package com.AFM.AML.Quiz.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizQestionOptionsRequest {
    private Quiz quiz;
    private List<QuestionMcqRequest> questionMcqRequestList;
}
