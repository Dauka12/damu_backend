package com.AFM.AML.Course.models.DTOs;

import lombok.Data;

import java.util.Date;

@Data
public class PaymentInfoDTO {
    private double progress_percentage;
    private String payment_type;
    private Date payment_date;
    private String status;
}
