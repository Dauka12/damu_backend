package com.AFM.AML.User.models.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoursesInfoDTO {
    private String iin;
    private String fullName;
    private List<UserCourseProgressDTO> courses;
    private List<UserCourseProgressDTO> completedCourses;
    private int totalCourses;
    private int completedCoursesCount;
}
