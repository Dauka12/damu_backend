package com.AFM.AML.Course.damu_dto.request.course_category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseCategoryCreateRequest {
    private int category_id;
    private String category_name;
}
