package com.AFM.AML.User.models;

import lombok.*;

@Data
public class MessageDTO {
    private String name;
    private String phoneNumber;
    private String email;
    private String comment;
    private Boolean isNews;
}
