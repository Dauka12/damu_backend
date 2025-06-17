package com.AFM.AML.Course.models.game;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AvatarInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String first_name;
    private String last_name;
    private String middle_name;
    private String date_of_birth;
    private String iin;
    private String start_date;
    private String postal_code;
    private String location;
    private String street_name;
    private String house_number;

}
