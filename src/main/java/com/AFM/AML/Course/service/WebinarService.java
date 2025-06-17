package com.AFM.AML.Course.service;

import com.AFM.AML.Course.models.UserWebinar;
import com.AFM.AML.Course.models.Webinar;
import com.AFM.AML.Course.repository.UserWebinarRepo;
import com.AFM.AML.Course.repository.WebinarRepo;
import com.AFM.AML.Minio.service.MinioService;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WebinarService {
    @Autowired
    WebinarRepo webinarRepo;
    @Autowired
    UserWebinarRepo userWebinarRepo;
    @Autowired
    MinioService minioService;
    @Autowired
    UserRepository userRepository;


    public ResponseEntity<?> createWebinar(Webinar webinar, MultipartFile multipartFile) throws ServerException, InvalidBucketNameException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, InvalidExpiresRangeException {
        String url = UUID.randomUUID().toString();
        minioService.uploadFile(multipartFile,url);
        webinar.setMinio_image(url);
        webinar.setImage(minioService.getFileUrl(url));
        List<User> users = userRepository.findAll();
        webinarRepo.save(webinar);
        for(User user: users){
            UserWebinar userWebinar = new UserWebinar().builder().user(user).status("available").webinar(webinar).build();
            userWebinarRepo.save(userWebinar);
        }
        return ResponseEntity.ok(webinar);
    }

    public ResponseEntity<?> updateStatsus(int user_id, int webinar_id){
        userWebinarRepo.updateStatus(user_id,webinar_id);
        return ResponseEntity.ok("zbs");
    }
    public ResponseEntity<?> updateStatus(Principal principal, int webinar_id)  {
        Optional<User> user = userRepository.findByEmail(principal.getName());
        Optional<Webinar> webinar = webinarRepo.findById(webinar_id);
        UserWebinar userWebinar = new UserWebinar();
        userWebinar.setUser(user.get());
        userWebinar.setWebinar(webinar.get());
        userWebinar.setStatus("process");
        userWebinarRepo.save(userWebinar);

        return ResponseEntity.ok("zbs");
    }

//    public ResponseEntity<?> getUserWebinar(){
//        Optional<User> user = userRepository.findByEmail(principal.getName());
//        return ResponseEntity.ok(userWebinarRepo.getUserWebinarByUserId(user.get().getUser_id()));
//    }
    public ResponseEntity<?> findal(Principal principal){
        Optional<User> user = userRepository.findByEmail(principal.getName());
        return ResponseEntity.ok(webinarRepo.getUserWebinarByUserId(user.get().getUser_id()));
    }
    public ResponseEntity<?> findall(){
        return ResponseEntity.ok(webinarRepo.getActiveWebinarsOrderedByDate());
    }

    public ResponseEntity<?> deleteWebinar(int userId, int webinarId) {
        userWebinarRepo.deleteByUserAndWebinar(userId,webinarId);
        return ResponseEntity.ok("zbs udalil");
    }

    public ResponseEntity<?> deleteWebinarById(int webinarId) {
        userWebinarRepo.deleteById(webinarId);
        webinarRepo.deleteById(webinarId);
        return ResponseEntity.ok("zbs");
    }
}
