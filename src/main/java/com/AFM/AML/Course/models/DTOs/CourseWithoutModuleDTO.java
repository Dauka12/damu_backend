package com.AFM.AML.Course.models.DTOs;

import com.AFM.AML.Course.models.CourseCategory;
import com.AFM.AML.Course.models.CourseComments;
import com.AFM.AML.Course.models.Module;
import com.AFM.AML.Course.models.UserCourse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CourseWithoutModuleDTO {
    private int course_id;
    private String course_name;
    private double course_price;
    private String course_image;
    private Double course_price_sale;
    private String course_for_member_of_the_system;
    private String duration;
    private Double rating;
    private String group_of_person;
    private String type_of_study;
    private Integer applied_for_study;


    private String what_course_represents;

    private String who_course_intended_for;

    private String what_is_duration;

    private String what_is_availability;

    private String what_is_agenda_of_course;

    private String what_you_will_get;

    private boolean draft;

    private boolean deleted;

    public CourseWithoutModuleDTO(int course_id, String course_name, String course_image, double course_price, String course_for_member_of_the_system,boolean draft, boolean deleted) {
        this.course_id = course_id;
        this.course_name = course_name;
        this.course_image = course_image;
        this.course_price = course_price;
        this.course_for_member_of_the_system = course_for_member_of_the_system;
        this.draft=draft;
        this.deleted=deleted;
    }

    public CourseWithoutModuleDTO(int course_id, String course_name, double course_price, String course_image, Double course_price_sale, String course_for_member_of_the_system, String duration, Double rating, String group_of_person, String type_of_study, Integer applied_for_study, String what_course_represents, String who_course_intended_for, String what_is_duration, String what_is_availability, String what_is_agenda_of_course, String what_you_will_get, boolean draft, boolean deleted) {
        this.course_id = course_id;
        this.course_name = course_name;
        this.course_price = course_price;
        this.course_image = course_image;
        this.course_price_sale = course_price_sale;
        this.course_for_member_of_the_system = course_for_member_of_the_system;
        this.duration = duration;
        this.rating = rating;
        this.group_of_person = group_of_person;
        this.type_of_study = type_of_study;
        this.applied_for_study = applied_for_study;
        this.what_course_represents = what_course_represents;
        this.who_course_intended_for = who_course_intended_for;
        this.what_is_duration = what_is_duration;
        this.what_is_availability = what_is_availability;
        this.what_is_agenda_of_course = what_is_agenda_of_course;
        this.what_you_will_get = what_you_will_get;
        this.draft = draft;
        this.deleted = deleted;
    }

    public int getCourse_id() {
        return course_id;
    }

    public void setCourse_id(int course_id) {
        this.course_id = course_id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public double getCourse_price() {
        return course_price;
    }

    public void setCourse_price(double course_price) {
        this.course_price = course_price;
    }

    public String getCourse_image() {
        return course_image;
    }

    public void setCourse_image(String course_image) {
        this.course_image = course_image;
    }

    public Double getCourse_price_sale() {
        return course_price_sale;
    }

    public void setCourse_price_sale(Double course_price_sale) {
        this.course_price_sale = course_price_sale;
    }

    public String getCourse_for_member_of_the_system() {
        return course_for_member_of_the_system;
    }

    public void setCourse_for_member_of_the_system(String course_for_member_of_the_system) {
        this.course_for_member_of_the_system = course_for_member_of_the_system;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getGroup_of_person() {
        return group_of_person;
    }

    public void setGroup_of_person(String group_of_person) {
        this.group_of_person = group_of_person;
    }

    public String getType_of_study() {
        return type_of_study;
    }

    public void setType_of_study(String type_of_study) {
        this.type_of_study = type_of_study;
    }

    public Integer getApplied_for_study() {
        return applied_for_study;
    }

    public void setApplied_for_study(Integer applied_for_study) {
        this.applied_for_study = applied_for_study;
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

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
