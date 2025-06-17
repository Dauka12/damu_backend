package com.AFM.AML.User.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCourseProgressDTO {
    private int courseId;
    private String courseName;
    private double progressPercentage;
    private String status;
    private String paymentType;
    private boolean isCompleted;
}
