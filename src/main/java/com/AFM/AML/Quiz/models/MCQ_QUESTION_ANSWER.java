package com.AFM.AML.Quiz.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MCQ_QUESTION_ANSWER {
    private int question;
    private int answer;
}
