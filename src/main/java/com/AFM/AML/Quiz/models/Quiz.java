package com.AFM.AML.Quiz.models;

import com.AFM.AML.Course.models.Module;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int quiz_id;
    private String quiz_title;
    private Double quiz_max_points;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    @JsonIgnore
    private Module module;

    @OneToMany(mappedBy = "quiz", fetch = FetchType.EAGER)
    private List<Question> quizList;
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Quiz_results> quizResults = new HashSet<>();

    @Override
    public String toString() {
        return "Quiz{" +
                "quiz_id=" + quiz_id +
                ", quiz_title='" + quiz_title + '\'' +
                ", quiz_max_points=" + quiz_max_points +
                ", quizList=" + quizList +
                '}';
    }
}
