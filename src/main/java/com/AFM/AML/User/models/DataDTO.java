package com.AFM.AML.User.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DataDTO {
    private Integer finished_courses;
    private Integer process_courses;
    private List<DateDataDTO> authenticated;
    private List<DateDataDTO> registration;
    private User user; // Add this field
}