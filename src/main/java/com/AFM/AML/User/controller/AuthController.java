package com.AFM.AML.User.controller;

import com.AFM.AML.User.models.*;
import com.AFM.AML.User.repository.LogRepository;
import com.AFM.AML.User.repository.UserRepository;
import com.AFM.AML.User.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/api/aml/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private final AuthenticationService service;
    @Autowired
    LogRepository logRepository;
    private final UserRepository userRepository;

    @GetMapping("/getData")
    public ResponseEntity<?> getData(){
        return ResponseEntity.ok(service.getDatas());
    }

    @GetMapping("/getData/{userId}")
    public ResponseEntity<?> getData(@PathVariable Integer userId){
        return ResponseEntity.ok(service.getDataByUserId(userId));
    }

    @PostMapping("/connectCheck")
    public ResponseEntity<?> connectCheck(String isConnected) {
        return ResponseEntity.ok("sda");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request)  {
        ResponseEntity<?> response = service.register(request);
        if (response.getStatusCodeValue() == 200) {
            Log log = Log.builder()
                    .activity("User registered")
                    .description("User registered with email: " + request.getEmail())
                    .date(new Date())
                    .user(userRepository.findByEmail(request.getEmail()).get())
                    .build();
            logRepository.save(log);
        } else {
            Log log = Log.builder()
                    .activity("User registration failed")
                    .description("User registration failed with email: " + request.getEmail())
                    .date(new Date())
                    .user(userRepository.findByEmail(request.getEmail()).get())
                    .build();
            logRepository.save(log);
        }
        return response;
    }
    @PostMapping("/authenticate")
    public ResponseEntity<?> register(
            @RequestBody AuthenticateRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/createRole")
    public String createRole(@RequestBody Role role){
        service.createRole(role);
        return "role created";
    }
    //Adil mnau userInfo  + dobav Bearar token
    @GetMapping("/userInfo")
    public ResponseEntity<?> userInfo(Principal principal){
        System.out.println("fdsfds");
        return service.getUserInfo(principal);
    }
    @GetMapping("/confirm-account")
    public RedirectView confirmToken(@RequestParam String token){
        return service.confirmEmail(token);
    }
    @PostMapping("/change_password")
    public ResponseEntity<?> changePassword(Principal principal,@RequestBody HashMap<String,String> password){
        return service.changePassword(principal,password.get("password"));
    }
    @PatchMapping("/change_user")
    public ResponseEntity<?> changePassword(Principal principal,@RequestBody User user){
        return service.changeUser(user,principal);
    }
    @PostMapping("/createJob")
    public ResponseEntity<?> createJopExp(Principal principal, @RequestBody JobExperience jobExperience){
        return service.createJob(principal,jobExperience);
    }
    @GetMapping("/getJob")
    public ResponseEntity<?> getJobs(Principal principal){
        return service.getUserJob(principal);
    }
    @DeleteMapping("/deleteJob/{job_id}")
    public String deleteJob(@PathVariable int job_id){
        return service.deleteJob(job_id);
    }

    @PostMapping("/sendMessageToMail")
    public ResponseEntity<?> sendMessageToMail(@RequestBody MessageDTO messageDTO)   {
        return ResponseEntity.ok("Message sent successfully");
    }
}