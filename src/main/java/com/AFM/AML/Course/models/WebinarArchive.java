package com.AFM.AML.Course.models;

import com.AFM.AML.Course.models.DTOs.WebinarArchiveDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebinarArchive {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer webinarA_id;
    private String webinar_name;
    private String webinar_description;
    private String webinar_url;
    private String webinar_image;
    private String webinar_date;
    private String webinar_for_member_of_the_system;

    public WebinarArchive(WebinarArchiveDTO webinarArchiveDTO){
        this.setWebinar_date(webinarArchiveDTO.getWebinar_date());
        this.setWebinar_name(webinarArchiveDTO.getWebinar_name());
        this.setWebinar_description(webinarArchiveDTO.getWebinar_description());
        this.setWebinar_url(webinarArchiveDTO.getWebinar_url());
        this.setWebinar_for_member_of_the_system(webinarArchiveDTO.getWebinar_for_member_of_the_system());
        this.setWebinar_image(webinarArchiveDTO.getWebinar_image());
    }
}
