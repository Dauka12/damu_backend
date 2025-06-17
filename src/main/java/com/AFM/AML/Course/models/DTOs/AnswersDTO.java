package com.AFM.AML.Course.models.DTOs;

import lombok.Data;

@Data
public class AnswersDTO {
    private Integer taskId;
    private Integer levelId;
    private Integer subLevelId;
    private Double isCorrect;

}
