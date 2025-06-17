package com.AFM.AML.Course.models.DTOs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class NewsDTO {
    private int id;
    private List description;
    private String kz_description;
    private String image;
    private String kz_image;
    private String name;
    private String kz_name;
    private Date date;
    private String type;
    private String lang;

    public NewsDTO(int id, String description, String image, String name, Date date, String type, String lang) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        this.id = id;
        this.description=objectMapper.readValue(description,List.class);
        this.image = image;
        this.name = name;
        this.date = date;
        this.type = type;
        this.lang = lang;
    }
}
