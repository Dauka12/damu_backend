package com.AFM.AML.Course.controller;

import com.AFM.AML.Course.models.Course;
import com.AFM.AML.Course.service.CourseService;
import com.AFM.AML.Course.service.QrCodeGenerator;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/checkQR")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@CrossOrigin(origins = "http://192.168.122.132:3000", allowCredentials = "true")
public class CertificateQrController {
    @Autowired
    QrCodeGenerator qrCodeGenerator;
    @Autowired
    CourseService courseService;
//    @Autowired
    @Autowired
    UserRepository userRepository;


    @RequestMapping(
            value = "/{user_id}/{cousre_id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public String checkQrFroUnique(@PathVariable int user_id,@PathVariable int cousre_id){
        Optional<Course> course = courseService.getCourseByID(cousre_id);
        Optional<User> user = userRepository.findById(user_id);
        return "Ползователь " + user.get().getLastname() + " " +
                user.get().getFirstname() + " действительно прошел курс " +
                course.get().getCourse_name();
    }
}
