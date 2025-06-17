package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "course_category_images")
@Getter
@Setter
public class CourseCategoryFiles {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "course_category_id", nullable = false)
    @JsonBackReference
    private CourseCategory courseCategory;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(columnDefinition = "TEXT")
    private String fileName;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;
}
