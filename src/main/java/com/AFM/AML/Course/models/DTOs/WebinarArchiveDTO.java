package com.AFM.AML.Course.models.DTOs;

import lombok.Data;

@Data
public class WebinarArchiveDTO {
    private String webinar_name;
    private String webinar_description;
    private String webinar_url;
    private String webinar_image;
    private String webinar_date;
    private String webinar_for_member_of_the_system;
}
