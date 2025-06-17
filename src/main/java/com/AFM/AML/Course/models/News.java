package com.AFM.AML.Course.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "json")
    private String description;
    @Column(columnDefinition = "json")
    private String kz_description;
    private String image;
    private String kz_image;
    private String name;
    private String kz_name;
    private String eng_name;
    private String eng_image;
    private String eng_description;
    private Date date;
    private String type;
    private String lang;


    public News(Integer id,String image,String kz_image,String name,String kz_name,Date date,String eng_name,String eng_image){
        this.id = id;
        this.image = image;
        this.kz_image = kz_image;
        this.name = name;
        this.kz_name = kz_name;
        this.date = date;
        this.eng_image = eng_image;
        this.eng_name = eng_name;
    }
}
