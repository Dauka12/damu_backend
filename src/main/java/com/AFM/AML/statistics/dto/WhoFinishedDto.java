package com.AFM.AML.statistics.dto;

import com.AFM.AML.User.models.Department;

import java.util.List;

public class WhoFinishedDto {
    public String full_name;
    public String department_name;
    public List<CourseFinishedInfoDto> finished_courses;

    public WhoFinishedDto() {
    }

    public WhoFinishedDto(String full_name, String department_name,  List<CourseFinishedInfoDto> finished_courses) {
        this.full_name = full_name;
        this.finished_courses = finished_courses;
        this.department_name = department_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public List<CourseFinishedInfoDto> getFinished_courses() {
        return finished_courses;
    }

    public void setFinished_courses(List<CourseFinishedInfoDto> finished_courses) {
        this.finished_courses = finished_courses;
    }
}
