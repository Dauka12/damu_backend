package com.AFM.AML.Course.models;

import com.AFM.AML.User.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserLessonCheck {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Boolean checked;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
//    @JsonIgnore
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lesson_id")
//    @JsonIgnore
    private Lesson lesson;

    @Override
    public String toString() {
        return "UserLessonCheck{" +
                "id=" + id +
                ", checked=" + checked +
                ", lesson_id=" + lesson.getLesson_id() +
                ", user_id=" + user.getUser_id() +
                '}';
    }
}
