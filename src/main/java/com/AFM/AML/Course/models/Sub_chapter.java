package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sub_chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sub_chapter_id;
    private String sub_chapter_name;
    private String sub_chapter_description;
    private String sub_chapter_image;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean is_active;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Chapter chapter;
//    @OneToMany(mappedBy = "subChapter")
//    private List<Sub_chapter_materials> subChapterMaterials;


    //Components
//    @OneToMany(mappedBy = "subChapter", cascade = CascadeType.ALL)
//    private List<ComponentEntry> componentEntries;

    @Override
    public String toString() {
        return "Sub_chapter{" +
                "sub_chapter_id=" + sub_chapter_id +
                ", sub_chapter_name='" + sub_chapter_name + '\'' +
                ", sub_chapter_description='" + sub_chapter_description + '\'' +
                ", sub_chapter_image='" + sub_chapter_image + '\'' +
//                ", subChapterMaterials=" + subChapterMaterials +
                '}';
    }
}
