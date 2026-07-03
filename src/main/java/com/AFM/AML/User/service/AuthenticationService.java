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
                .employment_status("ACTIVE")
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
            return ResponseEntity.status(401).body("Неправильный логин или пароль");
        }
        if (userRepository.existsByEmailandIsActiveTrue(request.getEmail()) == false) {
            return ResponseEntity.status(403).body("Почта не подтверждена");
        }
        
        try {
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
            
            // Создаем структуру ответа как раньше для совместимости с фронтендом
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", jwtToken);
            responseBody.put("user", user);
            
            return ResponseEntity.ok().body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Неправильный логин или пароль");
        }
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
    public ResponseEntity<?> changeUser(UserUpdateRequest user, Principal principal){
        if(principal == null){
            return ResponseEntity.badRequest().body("You should enter access token");
        }
        System.out.println(principal.getName());
        Optional<User> user1Optional = userRepository.findById(user.getUser_id());
        if (user1Optional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user1 = user1Optional.get();
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            user1.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getDerId() != null) {
            Der der = derRepository.findById(user.getDerId())
                    .orElseThrow(() -> new RuntimeException("Der not found, id=" + user.getDerId()));
            user1.setDer(der);
        }

        if (user.getDepartmentId() != null) {
            Department department = departmentRepository.findById(user.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found, id=" + user.getDepartmentId()));
            if (user.getDerId() != null && department.getDerId() != null && !department.getDerId().equals(user.getDerId())) {
                return ResponseEntity.badRequest().body("Department does not belong to selected DER");
            }
            user1.setDepartment(department);
        }

        if (user.getEmail() != null) {
            user1.setEmail(user.getEmail());
        }
        if (user.getFirstname() != null) {
            user1.setFirstname(user.getFirstname());
        }
        if (user.getLastname() != null) {
            user1.setLastname(user.getLastname());
        }
        if (user.getPatronymic() != null) {
            user1.setPatronymic(user.getPatronymic());
        }
        if (user.getPhone_number() != null) {
            user1.setPhone_number(user.getPhone_number());
        }
        if (user.getIin() != null) {
            user1.setIin(user.getIin());
        }
        if (user.getMember_of_the_system() != null) {
            user1.setMember_of_the_system(user.getMember_of_the_system());
        }
        if (user.getType_of_member() != null) {
            user1.setType_of_member(user.getType_of_member());
        }
        if (user.getEmployment_status() != null) {
            String employmentStatus = user.getEmployment_status().trim().toUpperCase(Locale.ROOT);
            if (!employmentStatus.equals("ACTIVE") && !employmentStatus.equals("FIRED")) {
                return ResponseEntity.badRequest().body("Invalid employment status");
            }
            user1.setEmployment_status(employmentStatus);
        }
        if (user.getJob_name() != null) {
            user1.setJob_name(user.getJob_name());
        }
        User savedUser = userRepository.save(user1);
        return ResponseEntity.ok(savedUser);
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
