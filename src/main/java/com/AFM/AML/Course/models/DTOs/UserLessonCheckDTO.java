package com.AFM.AML.Course.models.DTOs;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
public class UserLessonCheckDTO {
    int id;
    boolean checked;


}
