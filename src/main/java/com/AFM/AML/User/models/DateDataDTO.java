package com.AFM.AML.User.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Date;

@NoArgsConstructor
@Data
public class DateDataDTO {
    private String date;
    private Integer count;
    private String name;


    public DateDataDTO(String date, Integer count, String name) {
        this.date = date;
        this.count = count;
        this.name = name;
    }
}
