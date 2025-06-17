package com.AFM.AML.User.service;

import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int getCurrentUserId() {
        // Достаём текущую аутентификацию
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        // По умолчанию, getName() = email
        String email = authentication.getName();

        // Находим пользователя в БД
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found by email: " + email));

        return user.getUser_id();
    }
}
