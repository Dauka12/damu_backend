package com.AFM.AML.Course.models;

import com.AFM.AML.User.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
public class UserCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    private String language;
    public String getLanguage() {
        return language;
    }
    public void setLanguage(String language) {
        this.language = language;
    }


    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id")
    private Course course;
    private double progress_percentage;
    private String payment_type;
    private Date payment_date;
    private String status;
    private String fio;
    private String phone_number;
    private String email;
    private Date expired_date;
    @Column
    @javax.annotation.Nullable
    private Integer certificate_int;
    @Column    @Nullable

    private String date_certificate;
    @Column    @Nullable

    private String static_full_name;
    @Override
    public String toString() {
        return "UserCourse{" +
                "id=" + id +
                ", course=" + course +
                ", progress_percentage=" + progress_percentage +
                ", payment_type='" + payment_type + '\'' +
                ", payment_date=" + payment_date + '\"' +
                ", status=" + status +
                '}';
    }
}
