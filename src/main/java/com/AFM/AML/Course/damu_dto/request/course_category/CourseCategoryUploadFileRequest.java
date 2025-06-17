package com.AFM.AML.Course.damu_dto.request.course_category;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class CourseCategoryUploadFileRequest {
    @Schema(description = "Category ID", type = "integer")
    private int id;

    @Schema(description = "Course category images", type = "array", format = "binary")
    private List<MultipartFile> files;
}
