package com.AFM.AML.Course.models;

import com.AFM.AML.User.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int course_comment_id;
    private String comment;
    private int rate;
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Course course;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Override
    public String toString() {
        return "CourseComments{" +
                "course_comment_id=" + course_comment_id +
                ", comment='" + comment + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
