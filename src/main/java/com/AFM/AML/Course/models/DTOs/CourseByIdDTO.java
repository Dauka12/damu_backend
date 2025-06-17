package com.AFM.AML.Course.models.DTOs;

import com.AFM.AML.Course.models.Course;
import lombok.Data;

import java.util.Date;
@Data

public class CourseByIdDTO {
        private int id;
        private Course course;
        private double progress_percentage;
        private String payment_type;
        private Date payment_date;
        private String status;
    }

