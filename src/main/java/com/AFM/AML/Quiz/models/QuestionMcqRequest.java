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
public class QuestionMcqRequest {
    private Question question;
    private List<Mcq_option> mcqOptionList;
    private List<MatchingPair> matchingPairs;
}
