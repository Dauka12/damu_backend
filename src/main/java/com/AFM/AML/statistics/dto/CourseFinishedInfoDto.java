package com.AFM.AML.statistics.dto;

public class CourseFinishedInfoDto {
    private String courseName;
    private Integer timeSpentDays; // null, если date_certificate нет
    private Integer maxPoints;
    private Integer userPoints;
    private Double score;

    public CourseFinishedInfoDto() {
    }

    public CourseFinishedInfoDto(String courseName, Integer timeSpentDays, Integer maxPoints, Integer userPoints, Double score) {
        this.courseName = courseName;
        this.timeSpentDays = timeSpentDays;
        this.maxPoints = maxPoints;
        this.userPoints = userPoints;
        this.score = score;
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

    public Integer getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Integer maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Integer getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(Integer userPoints) {
        this.userPoints = userPoints;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
