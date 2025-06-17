package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
public class CourseWithoutChapter {
    private int course_id;
    private String course_name;
    private double course_price;
    private String course_image;
    private String course_for_member_of_the_system;
    private String duration;
    private Double rating;
    private String type_of_study;
    private CourseCategory courseCategory;
    private List<CourseComments> courseComments;
    private Double course_price_sale;
    private String group_of_person;
    private Integer applied_for_study;
    private String who_course_intended_for;
    private int views;
    private int completed_users;
}
