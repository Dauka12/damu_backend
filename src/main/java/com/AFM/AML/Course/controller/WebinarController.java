package com.AFM.AML.Course.controller;

import com.AFM.AML.Course.models.Webinar;
import com.AFM.AML.Course.service.WebinarService;
import com.AFM.AML.User.models.User;
import io.minio.errors.*;

import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://192.168.122.132:3000", allowCredentials = "true")
@RequestMapping("/api/aml/webinar")
@RequiredArgsConstructor
public class WebinarController {
    @Autowired
    WebinarService webinarService;

    @PostMapping("/createWebinar")
    public ResponseEntity<?> createWeb(@RequestParam String type,@RequestParam String name,@RequestParam String description
            , @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date date, @RequestParam String link,@RequestParam String webinar_for_member_of_the_system , @RequestParam MultipartFile multipartFile) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidExpiresRangeException {
        Webinar webinar = new Webinar().builder().date(date).link(link).type(type).is_active(true).name(name).description(description).webinar_for_member_of_the_system(webinar_for_member_of_the_system).build();
        return webinarService.createWebinar(webinar,multipartFile);
    }

    @PostMapping("/saveUser/webinar/{webinar_id}")
    public ResponseEntity<?> addToWebinar(Principal principal, @PathVariable int webinar_id)   {
        return webinarService.updateStatus(principal,webinar_id);
    }

    @PostMapping("/updateStatusToWebinarId/{webinar_id}")
    public ResponseEntity<?> addtoWebinar(@PathVariable int webinar_id, Principal principal)   {
        return webinarService.updateStatus(principal,webinar_id);
    }

    @DeleteMapping("/deleteWebinarFromUser/{user_id}/{webinar_id}")
    public ResponseEntity<?> delete(@PathVariable int user_id, @PathVariable int webinar_id){
        return webinarService.deleteWebinar(user_id,webinar_id);
    }

    @DeleteMapping("/deleteWebinar/{webinar_id}")
    public ResponseEntity<?> deleteWebinar(@PathVariable int webinar_id){
        return webinarService.deleteWebinarById(webinar_id);
    }

//    @GetMapping("/getUserWebinar")
//    public ResponseEntity<?> getUserWeb(Principal principal){
//        return webinarService.getUserWebinar(principal);
//    }
    @GetMapping("/getWebinars")
    public ResponseEntity<?> getUserWeb(){
        return webinarService.findall();
    }
    @GetMapping("/getWebinarss")
    public ResponseEntity<?> getUserWeb(Principal principal){
        return webinarService.findal(principal);
    }
}
