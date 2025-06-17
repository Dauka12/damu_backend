package com.AFM.AML.User.models.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DerWithDepartmentsDTO {
    private int id;
    private String name_rus;
    private String name_kaz;
    private List<DepartmentDTO> departments;
}
