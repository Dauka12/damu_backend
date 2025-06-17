package com.AFM.AML.User.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.AFM.AML.User.models.dto.UserCoursesInfoDTO;
import com.AFM.AML.User.service.UserCoursesService;

@RestController
@CrossOrigin(origins = "*") // Открытый доступ для всех источников
@RequestMapping("/api/public")
public class PublicUserController {

    @Autowired
    private UserCoursesService userCoursesService;

    @GetMapping("/{iin}")
    public ResponseEntity<?> getUserCoursesByIin(@PathVariable String iin) {
        // Проверяем корректность ИИН (12 цифр)
        if (iin == null || !iin.matches("\\d{12}")) {
            return ResponseEntity.badRequest().body("Некорректный ИИН. ИИН должен содержать 12 цифр.");
        }

        Optional<UserCoursesInfoDTO> userCoursesInfo = userCoursesService.getUserCoursesByIin(iin);
        
        if (!userCoursesInfo.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(userCoursesInfo.get());
    }
}
