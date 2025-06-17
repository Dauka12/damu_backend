package com.AFM.AML.Course.controller;

import  com.AFM.AML.Course.models.Event;
import com.AFM.AML.Course.models.EventRequest;
import com.AFM.AML.Course.service.EventService;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://192.168.122.132:3000", allowCredentials = "true")
@RequestMapping("/api/aml/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/makeRequestEvent")
    public ResponseEntity<?> makeRequestEvent(@RequestParam Integer id,@RequestParam String username,@RequestParam String phoneNumber,
                                                         @RequestParam String email,@RequestParam String comment) {
        return ResponseEntity.ok(eventService.makeRequestEvent(id,username,phoneNumber,email,comment));
    }

    @GetMapping("/getRequests")
    public ResponseEntity<?> getRequests(@RequestParam Integer id) {
        return ResponseEntity.ok(eventService.getRequests(id));
    }

    @GetMapping("/getAllRequests")
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(eventService.getAllRequests());
    }

    @PostMapping("/createEvent")
    public ResponseEntity<?> createEvent(
            @RequestParam String ru_name,
            @RequestParam String kz_name,
            @RequestParam String ru_description,
            @RequestParam String kz_description,
            @RequestParam String location,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam MultipartFile coverImage,
            @RequestParam MultipartFile logoImage,
            @RequestParam String typeOfStudy,
            @RequestParam String formatOfStudy,
            @RequestParam String program,
            @RequestParam String speakers) throws Exception {
        try {
            return ResponseEntity.ok(eventService.createEvent(ru_name, kz_name, ru_description, kz_description,
                    location, startDate, endDate, coverImage,
                    logoImage, typeOfStudy, formatOfStudy,
                    program, speakers));
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @PutMapping("/editEvent")
    public ResponseEntity<?> editEvent(@RequestParam Integer id,@RequestParam String ru_name, @RequestParam String kz_name, @RequestParam String ru_description,
                                   @RequestParam String kz_description, @RequestParam String location, @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
                                   @RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate, @RequestParam(required = false) MultipartFile coverImage, @RequestParam(required = false) MultipartFile logoImage,
                                   @RequestParam String typeOfStudy, @RequestParam String formatOfStudy, @RequestParam String program,
                                   @RequestParam String speakers)  {
        try{
            return ResponseEntity.ok(eventService.updateEvent(id, ru_name,  kz_name,  ru_description,  kz_description,
                    location,  startDate,  endDate,  coverImage,
                    logoImage,  typeOfStudy, formatOfStudy,
                    program, speakers));
        } catch (ServerException e) {
            throw new RuntimeException(e);
        } catch (InvalidBucketNameException e) {
            throw new RuntimeException(e);
        } catch (InsufficientDataException e) {
            throw new RuntimeException(e);
        } catch (ErrorResponseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (InvalidResponseException e) {
            throw new RuntimeException(e);
        } catch (XmlParserException e) {
            throw new RuntimeException(e);
        } catch (InternalException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/deleteEvent")
    public ResponseEntity<?> deleteEvent(@RequestParam Integer id)  {
        eventService.deleteEvent(id);
        return ResponseEntity.ok("Event deleted successfully");
    }

    @GetMapping("/getAllEvents")
    public ResponseEntity<?> findAll(){
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/getEventById")
    public ResponseEntity<?> findById(@RequestParam Integer id)  {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/getUncomingEvents")
    public ResponseEntity<?> findUncomingEvents(){
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    @GetMapping("/getPassingEvents")
    public ResponseEntity<?> findPassingEvents(){
        return ResponseEntity.ok(eventService.getPassingEvents());
    }
}
