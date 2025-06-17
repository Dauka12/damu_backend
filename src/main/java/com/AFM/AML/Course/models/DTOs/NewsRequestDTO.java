package com.AFM.AML.Course.models.DTOs;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NewsRequestDTO {
    String kz_description;
    String description;
    MultipartFile kz_file;
    MultipartFile file;
}
