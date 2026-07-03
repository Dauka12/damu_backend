package com.AFM.AML.User.models;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private int user_id;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String email;
    private String iin;
    private String phone_number;
    private String password;
    private String member_of_the_system;
    private String type_of_member;
    private String employment_status;
    private String job_name;
    private Integer derId;
    private Integer departmentId;
}
