package com.AFM.AML.Course.models.DTOs;

import com.AFM.AML.Course.models.CourseWithoutChapter;
import com.AFM.AML.User.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
public class UserCourseDTO {
    private int id;
    private CourseWithoutChapter courseDTO;
    private PaymentInfoDTO paymentInfo;
    private int shortStatus;
    // In UserCourseDTO.java
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

//    private double progress_percentage;
//    private String payment_type;
//    private Date payment_date;
//    private String status;
}
