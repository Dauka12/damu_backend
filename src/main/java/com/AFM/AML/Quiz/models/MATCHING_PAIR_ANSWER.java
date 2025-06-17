package com.AFM.AML.Quiz.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MATCHING_PAIR_ANSWER {
    private int question;
    private String left_part;
    private String right_part;
}
