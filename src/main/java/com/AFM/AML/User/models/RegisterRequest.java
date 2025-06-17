package com.AFM.AML.User.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String firstname;
    private String lastname;
    private String patronymic;
    private String email;
    private String iin; // Individual Identification Number
    private String phone_number;
    private String password;
    private String gender;
    private String member_of_the_system;
    private Integer derId;
    private Integer departmentId;
}
