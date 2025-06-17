package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.List;

@Data
@Builder
@Entity
@Table(name = "course_category")
@NoArgsConstructor
@AllArgsConstructor
public class CourseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int category_id;
    @Column(columnDefinition = "text")
    private String category_image;
    @Column(columnDefinition = "text")
    private String minio_image_name;
    @Column(columnDefinition = "text")
    private String category_name;
    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;
    @OneToMany(mappedBy = "courseCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Where(clause = "is_deleted = false")
    private List<CourseCategoryFiles> files;
}
