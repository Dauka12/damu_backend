package com.AFM.AML.Course.models;

import com.AFM.AML.User.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "course")
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int course_id;
    @Column(nullable = false)
    private String course_name;
    @Column(nullable = false)
    private double course_price;
    @Column(columnDefinition = "text")
    private String course_image;
    @Column(name = "course_price_sale")
    private Double course_price_sale;
    private String course_for_member_of_the_system;
    private String duration;
    private Double rating;
    private String group_of_person;
//    private String type_of_study;
    private Integer applied_for_study;

    @Column(nullable = true)
    private String language;

    @Column(columnDefinition = "text")
    private String what_course_represents;
    @Column(columnDefinition = "text")
    private String who_course_intended_for;
    @Column(columnDefinition = "text")
    private String what_is_duration;
    @Column(columnDefinition = "text")
    private String what_is_availability;
    @Column(columnDefinition = "text")
    private String what_is_agenda_of_course;
    @Column(columnDefinition = "text")
    private String what_you_will_get;
    @Column(nullable = true)
    private boolean draft;
    @Column(nullable = true , columnDefinition = "integer default 0")
    private int views;
    @Formula("(SELECT COUNT(*) FROM user_course uc WHERE uc.course_id = course_id AND uc.status = 'finished')")
    private int completed_users;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CourseCategory courseCategory;
    @OneToMany(mappedBy = "course",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    @OrderBy("id ASC")
    private Set<UserCourse> userCourses = new HashSet<>();
//    @OneToMany(mappedBy = "course")
//    private List<Chapter> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    @OrderBy("module_id ASC")
    private List<Module> modules = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private List<CourseComments> courseComments;


    @Override
    public String toString() {
        return "Course{" +
                "course_id=" + course_id +
                ", course_name='" + course_name + '\'' +
                ", course_price=" + course_price +
                ", course_image='" + course_image + '\'' +
                ", course_for_member_of_the_system='" + course_for_member_of_the_system + '\'' +
                ", courseCategory=" + courseCategory +
                ", courseComments=" + courseComments +
//                ", courseChapter=" + chapters +
                '}';
    }
}
