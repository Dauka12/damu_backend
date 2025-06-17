package com.AFM.AML.User.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.AFM.AML.Course.models.UserCourse;
import com.AFM.AML.Course.repository.UserCourseRepository;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.models.dto.UserCourseProgressDTO;
import com.AFM.AML.User.models.dto.UserCoursesInfoDTO;
import com.AFM.AML.User.repository.UserRepository;

@Service
public class UserCoursesService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCourseRepository userCourseRepository;

    public Optional<UserCoursesInfoDTO> getUserCoursesByIin(String iin) {
        Optional<User> userOpt = userRepository.findByIin(iin);
        
        if (!userOpt.isPresent()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        List<UserCourse> userCourses = userCourseRepository.findByUser(user);
        
        // Преобразуем все курсы в DTO
        List<UserCourseProgressDTO> courseDTOs = userCourses.stream()
            .map(userCourse -> new UserCourseProgressDTO(
                userCourse.getCourse().getCourse_id(),
                userCourse.getCourse().getCourse_name(),
                userCourse.getProgress_percentage(),
                userCourse.getStatus(),
                userCourse.getPayment_type(),
                userCourse.getProgress_percentage() >= 100.0
            ))
            .collect(Collectors.toList());
        
        // Фильтруем завершенные курсы
        List<UserCourseProgressDTO> completedCourses = courseDTOs.stream()
            .filter(course -> course.getProgressPercentage() >= 100.0)
            .collect(Collectors.toList());
        
        String fullName = user.getFirstname() + " " + user.getLastname();
        if (user.getPatronymic() != null && !user.getPatronymic().isEmpty()) {
            fullName += " " + user.getPatronymic();
        }
        
        UserCoursesInfoDTO result = new UserCoursesInfoDTO(
            user.getIin(),
            fullName,
            courseDTOs,
            completedCourses,
            courseDTOs.size(),
            completedCourses.size()
        );
        
        return Optional.of(result);
    }
}
