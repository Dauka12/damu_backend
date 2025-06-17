package com.AFM.AML.Course.service;

import com.AFM.AML.Course.models.DTOs.EventRequestRespondDTO;
import com.AFM.AML.Course.models.Event;
import com.AFM.AML.Course.models.EventRequest;
import com.AFM.AML.Course.repository.EventRepo;
import com.AFM.AML.Course.repository.EventRequestRepo;
import com.AFM.AML.Course.utils.ImageUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRequestRepo eventRequestRepo;

    private final EventRepo eventRepository;

    private final ImageUtil imageUtil;

    private final ObjectMapper objectMapper;

    public EventRequest makeRequestEvent(Integer id,String username, String phoneNumber, String email, String comment) {
        Event event=eventRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Event not found"));
        EventRequest eventRequest=new EventRequest();
        eventRequest.setEvent(event);
        eventRequest.setUsername(username);
        eventRequest.setPhoneNumber(phoneNumber);
        eventRequest.setEmail(email);
        eventRequest.setComment(comment);
        return eventRequestRepo.save(eventRequest);
    }

    public List<EventRequest> getRequests(Integer id) {
        return eventRequestRepo.findByEventId(id);
    }

    public List<EventRequestRespondDTO> getAllRequests() {
        List<EventRequest> requests = eventRequestRepo.findAll();
        return requests.stream().map(EventRequestRespondDTO::new).toList();
    }

    public List<Event> getUpcomingEvents(){
        return eventRepository.findUpcomingEvents(new Date());
    }

    public List<Event> getPassingEvents(){
        return eventRepository.findPassingEvents(new Date());
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Integer id) {
        return eventRepository.findById(id);
    }

    public Event createEvent(String ru_name, String kz_name, String ru_description, String kz_description,
                             String location, Date startDate, Date endDate, MultipartFile coverImage,
                             MultipartFile logoImage, String typeOfStudy,String formatOfStudy,
                             String program,String speakers) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Event event = new Event();
        event.setRu_name(ru_name);
        event.setKz_name(kz_name);
        event.setRu_description(ru_description);
        event.setKz_description(kz_description);
        event.setLocation(location);
        event.setStartDate(startDate);
        event.setEndDate(endDate);
        event.setCoverImage(imageUtil.fileToMinio(coverImage));
        event.setLogoImage(imageUtil.fileToMinio(logoImage));
        event.setTypeOfStudy(typeOfStudy);
        event.setFormatOfStudy(formatOfStudy);
        List<Map<String,String>> ls1=objectMapper.readValue(program,List.class);
        event.setProgram(objectMapper.writeValueAsString(ls1));
        List<Map<String,String>> ls=objectMapper.readValue(speakers,List.class);
        for (Map<String, String> l : ls) {
            if (l.get("image").startsWith("data:")) {
                l.put("image", imageUtil.imgAndFiles(l.get("image")));
            } else {
                l.put("image", "");
            }
        }
        event.setSpeakers(objectMapper.writeValueAsString(ls));
        return eventRepository.save(event);
    }

    public Event updateEvent(Integer id,String ru_name, String kz_name, String ru_description, String kz_description,
                             String location, Date startDate, Date endDate, MultipartFile coverImage,
                             MultipartFile logoImage, String typeOfStudy,String formatOfStudy,
                             String program,String speakers) throws IOException, ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Optional<Event> eventOptional = eventRepository.findById(id);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setRu_name(ru_name);
            event.setKz_name(kz_name);
            event.setRu_description(ru_description);
            event.setKz_description(kz_description);
            event.setLocation(location);
            event.setStartDate(startDate);
            event.setEndDate(endDate);
            if(coverImage != null){
                event.setCoverImage(imageUtil.fileToMinio(coverImage));
            }
            if(logoImage != null){
                event.setLogoImage(imageUtil.fileToMinio(logoImage));
            }
            event.setTypeOfStudy(typeOfStudy);
            event.setFormatOfStudy(formatOfStudy);
            List<Map<String,String>> ls1=objectMapper.readValue(program,List.class);
            event.setProgram(objectMapper.writeValueAsString(ls1));
            List<Map<String,String>> ls=objectMapper.readValue(speakers,List.class);
            for (Map<String, String> l : ls) {
                if (l.get("image").startsWith("data:")) {
                    l.put("image", imageUtil.imgAndFiles(l.get("image")));
                }
            }
            event.setSpeakers(objectMapper.writeValueAsString(ls));
            return eventRepository.save(event);
        } else {
            throw new RuntimeException("Event not found with id " + id);
        }
    }

    public void deleteEvent(Integer id) {
        eventRepository.deleteById(id);
    }
}
