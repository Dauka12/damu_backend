package com.AFM.AML.Course.models;


import com.AFM.AML.Quiz.models.Quiz;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Table(name = "module")
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int module_id;
    private String name;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean is_active;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Course course;

    @OneToMany(mappedBy = "module")
    @OrderBy("lesson_id ASC")
    private List<Lesson> lessons;

    @OneToOne(mappedBy = "module")
    private Quiz quiz;
}
