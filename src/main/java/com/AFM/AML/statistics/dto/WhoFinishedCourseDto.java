package com.AFM.AML.statistics.dto;


public class WhoFinishedCourseDto {
    public String full_name;
    public String department_name;
    public Integer maxPoints;
    public Integer userPoints;
    public Double score;

    public WhoFinishedCourseDto() {
    }

    public WhoFinishedCourseDto(String full_name, String department_name, Integer maxPoints, Integer userPoints, Double score) {
        this.full_name = full_name;
        this.department_name = department_name;
        this.maxPoints = maxPoints;
        this.userPoints = userPoints;
        this.score = score;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
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
