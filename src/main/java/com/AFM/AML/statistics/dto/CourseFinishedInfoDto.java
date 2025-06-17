package com.AFM.AML.statistics.dto;

public class CourseFinishedInfoDto {
    private String courseName;
    private Integer timeSpentDays; // null, если date_certificate нет

    public CourseFinishedInfoDto() {
    }

    public CourseFinishedInfoDto(String courseName, Integer timeSpentDays) {
        this.courseName = courseName;
        this.timeSpentDays = timeSpentDays;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getTimeSpentDays() {
        return timeSpentDays;
    }

    public void setTimeSpentDays(Integer timeSpentDays) {
        this.timeSpentDays = timeSpentDays;
    }
}
