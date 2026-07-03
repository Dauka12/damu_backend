package com.AFM.AML.Course.models.DTOs;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int user_id;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String email;
    private String employment_status;
}
