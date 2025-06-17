package com.AFM.AML.User.service;

import com.AFM.AML.Course.repository.UserCourseRepository;
import com.AFM.AML.Course.service.CourseService;
import com.AFM.AML.User.exception.NotFoundException;
import com.AFM.AML.User.models.*;
import com.AFM.AML.User.repository.JobExpRepo;
import com.AFM.AML.User.repository.LogRepository;
import com.AFM.AML.User.repository.RoleRepository;
import com.AFM.AML.User.repository.UserRepository;
import com.AFM.AML.config.JwtService;


import com.AFM.AML.statistics.repository.DepartmentRepository;
import com.AFM.AML.statistics.repository.DerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AuthenticationService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    LogRepository logRepository;
    @Autowired
    JwtService jwtService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JobExpRepo jobExpRepo;
    @Autowired
    CourseService courseService;

    @Autowired
    UserCourseRepository userCourseRepository;
    @Autowired
    private DerRepository derRepository;
    @Autowired
    private DepartmentRepository departmentRepository;


    public void createRole(Role role){
        roleRepository.save(role);
    }

    public DataDTO getDatas(){
        DataDTO dataDTO = new DataDTO();
        dataDTO.setProcess_courses(userCourseRepository.getCountByStatus("process"));
        dataDTO.setFinished_courses(userCourseRepository.getCountByStatus("finished"));
        List<Object[]> results = logRepository.countLogsByMonthAndActivity("authenticated");
        List<DateDataDTO> dateDataDTOs = new ArrayList<>();
        for (Object[] result : results) {
            String date = (String) result[0]; // Месяц (например, "2024-01")
            Long count = ((Number) result[1]).longValue(); // Количество логов
            String name = (String) result[2]; // Имя активности

            dateDataDTOs.add(new DateDataDTO(date, count.intValue(), name));
        }
        dataDTO.setAuthenticated(dateDataDTOs);
        List<Object[]> res = logRepository.countLogsByMonthAndActivity("registered");
        List<DateDataDTO> dateDataDTOsreg = new ArrayList<>();
        for (Object[] result : res) {
            String date = (String) result[0]; // Месяц (например, "2024-01")
            Long count = ((Number) result[1]).longValue(); // Количество логов
            String name = (String) result[2]; // Имя активности

            dateDataDTOsreg.add(new DateDataDTO(date, count.intValue(), name));
        }
        dataDTO.setRegistration(dateDataDTOsreg);
        return dataDTO;
    }

    public DataDTO getDataByUserId(Integer userId){
        DataDTO dataDTO = new DataDTO();
        dataDTO.setProcess_courses(userCourseRepository.getCountByStatusAndUser("process",userId));
        dataDTO.setFinished_courses(userCourseRepository.getCountByStatusAndUser("finished",userId));

        // Get user data and add it to the response
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        dataDTO.setUser(user);

        List<Object[]> results = logRepository.countLogsByMonthAndActivityByUser("authenticated",userId);
        List<DateDataDTO> dateDataDTOs = new ArrayList<>();
        for (Object[] result : results) {
            String date = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            String name = (String) result[2];

            dateDataDTOs.add(new DateDataDTO(date, count.intValue(), name));
        }
        dataDTO.setAuthenticated(dateDataDTOs);

        List<Object[]> res = logRepository.countLogsByMonthAndActivityByUser("registered",userId);
        List<DateDataDTO> dateDataDTOsreg = new ArrayList<>();
        for (Object[] result : res) {
            String date = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            String name = (String) result[2];

            dateDataDTOsreg.add(new DateDataDTO(date, count.intValue(), name));
        }
        dataDTO.setRegistration(dateDataDTOsreg);
        return dataDTO;
    }
    
//register
    public ResponseEntity<?> register(RegisterRequest request)  {
        Role role = roleRepository.findByName(ERole.ROLE_STUDENT);
        if(userRepository.existsByEmail(request.getEmail())){
            System.out.println(userRepository.existsByEmail(request.getEmail()));
            return ResponseEntity
                    .badRequest()
                    .body("Email is already Taken");
        }
        if (!request.getIin().matches("\\d{12}")) {
            return ResponseEntity
                    .badRequest()
                    .body("IIN must be exactly 12 digits");
        } else if (userRepository.findByIin(request.getIin()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("IIN is already Taken");
        }
        String confirmationToken = UUID.randomUUID().toString();

        Der derEntity = null;
        if (request.getDerId() != null) {
            derEntity = derRepository.findById(request.getDerId())
                    .orElseThrow(() -> new RuntimeException("Der not found, id=" + request.getDerId()));
        }

        Department departmentEntity = null;
        if (request.getDepartmentId() != null) {
            departmentEntity = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found, id=" + request.getDepartmentId()));
        }

        User user = User.builder()
                .email(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone_number(request.getPhone_number())
                .role(role)
                .patronymic(request.getPatronymic())
                .member_of_the_system(request.getMember_of_the_system())
                .iin(request.getIin())
                .der(derEntity)
                .department(departmentEntity)
                .is_active(true)
                .verificationCode(confirmationToken)
                .build();

        userRepository.save(user);
        jwtService.generateToken(user);
        Log log = new Log(); 
        log.setActivity("registered");
        log.setUser(user);
        log.setDescription("User with email " + user.getEmail() + " registered");
        Date date = new Date();
        log.setDate(date);
        logRepository.save(log);
//        courseService.saveCourseAvailableForUser(user);
        return ResponseEntity.ok(AuthenticationResponse.builder().token("Please verify your User email").build());
    }


    public ResponseEntity<?> authenticate(AuthenticateRequest request) {
        if(userRepository.existsByEmail(request.getEmail()) == false) {
            return ResponseEntity.badRequest().body("Email or Password is incorrect");
        }
        if (userRepository.existsByEmailandIsActiveTrue(request.getEmail()) == false) {
            return ResponseEntity.badRequest().body("Email is not verified yet");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        Log log = new Log();
        log.setActivity("authenticated");
        log.setUser(user);
        log.setDescription("User with email " + user.getEmail() + " authenticated");
        Date date = new Date();
        log.setDate(date);
        logRepository.save(log);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwtToken)
                .user(user)
                .build());
    }
    public RedirectView confirmEmail(String confirmationToken) {
        User user = userRepository.findByVerificationCode(confirmationToken);
        if(user != null)
        {
            user.set_active(true);
            userRepository.save(user);
            return new RedirectView("http://192.168.122.132:3000/login");
        }
        return null;
    }
    public ResponseEntity<?> getUserInfo(Principal principal){
        if(principal == null){
            return ResponseEntity.badRequest().body("You should enter access token");
        }
        return ResponseEntity.ok().body(userRepository.findByEmail(principal.getName()));
    }
    public ResponseEntity<?> changePassword(Principal principal,String password){
        if(principal == null){
            return ResponseEntity.badRequest().body("You should enter access token");
        }
        System.out.println(principal.getName());
        Optional<User> user = userRepository.findByEmail(principal.getName());
        user.get().setPassword(passwordEncoder.encode(password));
        userRepository.save(user.get());
        return ResponseEntity.ok("Password changed");
    }
    public ResponseEntity<?> changeUser(User user, Principal principal){
        if(principal == null){
            return ResponseEntity.badRequest().body("You should enter access token");
        }
        System.out.println(principal.getName());
        Optional<User> user1 = userRepository.findById(user.getUser_id());
        if(user.getPassword().length() > 30){
            user1.get().setPassword(user.getPassword());
        }else {
            user1.get().setPassword(passwordEncoder.encode(user.getPassword()));
        }
        user1.get().setEmail(user.getEmail());
        user1.get().setFirstname(user.getFirstname());
        user1.get().setLastname(user.getLastname());
        user1.get().setPatronymic(user.getPatronymic());
        user1.get().setMember_of_the_system(user.getMember_of_the_system());
        user1.get().setType_of_member(user.getType_of_member());
        user1.get().setJob_name(user.getJob_name());
        userRepository.save(user1.get());
        return ResponseEntity.ok("User changed");
    }
    public ResponseEntity<?> createJob(Principal principal,JobExperience jobExperience){
        Optional<User> user = userRepository.findByEmail(principal.getName());
        jobExperience.setUser(user.get());
        jobExpRepo.save(jobExperience);
        return ResponseEntity.ok().body(jobExperience);
    }

    public ResponseEntity<?> getUserJob(Principal principal) {
        Optional<User> user = userRepository.findByEmail(principal.getName());
        List<JobExperience> jobExperienceList = jobExpRepo.findByUserId(user.get().getUser_id());
        return ResponseEntity.ok(jobExperienceList);
    }
    public String deleteJob(int job_id){
        jobExpRepo.deleteById(job_id);
        return "job by id" + job_id + " deleted";
    }


}
