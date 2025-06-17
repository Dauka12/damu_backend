package com.AFM.AML.statistics.dto;


public class WhoFinishedCourseDto {
    public String full_name;
    public String department_name;

    public WhoFinishedCourseDto() {
    }

    public WhoFinishedCourseDto(String full_name, String department_name) {
        this.full_name = full_name;
        this.department_name = department_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }
}
