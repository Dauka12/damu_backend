package com.AFM.AML.Course.models.DTOs;

public class CourseNoBasicInfoDTO {
    private int id;
    private String what_course_represents;
    private String who_course_intended_for;
    private String what_is_duration;
    private String what_is_availability;
    private String what_is_agenda_of_course;
    private String what_you_will_get;
    public CourseNoBasicInfoDTO() {

    }

    public CourseNoBasicInfoDTO(String what_course_represents, String who_course_intended_for, String what_is_duration, String what_is_availability, String what_is_agenda_of_course, String what_you_will_get) {
        this.what_course_represents = what_course_represents;
        this.who_course_intended_for = who_course_intended_for;
        this.what_is_duration = what_is_duration;
        this.what_is_availability = what_is_availability;
        this.what_is_agenda_of_course = what_is_agenda_of_course;
        this.what_you_will_get = what_you_will_get;
    }

    public String getWhat_course_represents() {
        return what_course_represents;
    }

    public void setWhat_course_represents(String what_course_represents) {
        this.what_course_represents = what_course_represents;
    }

    public String getWho_course_intended_for() {
        return who_course_intended_for;
    }

    public void setWho_course_intended_for(String who_course_intended_for) {
        this.who_course_intended_for = who_course_intended_for;
    }

    public String getWhat_is_duration() {
        return what_is_duration;
    }

    public void setWhat_is_duration(String what_is_duration) {
        this.what_is_duration = what_is_duration;
    }

    public String getWhat_is_availability() {
        return what_is_availability;
    }

    public void setWhat_is_availability(String what_is_availability) {
        this.what_is_availability = what_is_availability;
    }

    public String getWhat_is_agenda_of_course() {
        return what_is_agenda_of_course;
    }

    public void setWhat_is_agenda_of_course(String what_is_agenda_of_course) {
        this.what_is_agenda_of_course = what_is_agenda_of_course;
    }

    public String getWhat_you_will_get() {
        return what_you_will_get;
    }

    public void setWhat_you_will_get(String what_you_will_get) {
        this.what_you_will_get = what_you_will_get;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
