package com.AFM.AML.Course.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.N;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Sub_chapter_materials {
    @Id
    private int sub_chapter_materials_id;
    private String material_type;
    private String material_type_url;
    @ManyToOne
    @JoinColumn(name = "sub_chapter_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Sub_chapter subChapter;

    @Override
    public String toString() {
        return "Sub_chapter_materials{" +
                "sub_chapter_materials_id=" + sub_chapter_materials_id +
                ", material_type='" + material_type + '\'' +
                ", material_type_url='" + material_type_url + '\'' +
                '}';
    }
}
