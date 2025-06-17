package com.AFM.AML.Course.models.DTOs;

import com.AFM.AML.Course.models.EventRequest;
import lombok.Data;

@Data
public class EventRequestRespondDTO {
    private Integer id;
    private String username;
    private String phoneNumber;
    private String email;
    private String comment;
    private Integer eventId;
    private String eventKzName;
    private String eventRuName;

    public EventRequestRespondDTO(EventRequest eventRequest) {
        this.id = eventRequest.getId();
        this.username = eventRequest.getUsername();
        this.phoneNumber = eventRequest.getPhoneNumber();
        this.email = eventRequest.getEmail();
        this.comment = eventRequest.getComment();
        this.eventId = eventRequest.getEvent().getId();
        this.eventKzName=eventRequest.getEvent().getKz_name();
        this.eventRuName=eventRequest.getEvent().getRu_name();
    }
}
