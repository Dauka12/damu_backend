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

@Table(name = "chapter")
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chapter_id;
    private String chapter_name;
    private String chapter_description;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean is_active;
    @Column(columnDefinition = "text")
    private String chapter_image;
    @Column(columnDefinition = "text")
    private String minio_image_name;



    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Course course;
    @OneToMany(mappedBy = "chapter")
    @JsonIgnore
    private List<Sub_chapter> subChapter;

    @Override
    public String toString() {
        return "Chapter{" +
                "chapter_id=" + chapter_id +
                ", chapter_name='" + chapter_name + '\'' +
                ", chapter_description='" + chapter_description + '\'' +
                ", chapter_image='" + chapter_image + '\'' +
                ", subChapter=" + subChapter +
                '}';
    }
}
