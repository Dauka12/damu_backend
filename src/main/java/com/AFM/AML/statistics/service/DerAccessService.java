package com.AFM.AML.statistics.service;

import com.AFM.AML.User.models.Der;
import com.AFM.AML.User.models.User;
import com.AFM.AML.User.repository.UserRepository;
import com.AFM.AML.statistics.model.DerAccess;
import com.AFM.AML.statistics.repository.DerAccessRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DerAccessService {

    private final DerAccessRepository derAccessRepository;
    private final UserRepository userRepository;

    public DerAccessService(DerAccessRepository derAccessRepository, UserRepository userRepository) {
        this.derAccessRepository = derAccessRepository;
        this.userRepository = userRepository;
    }

    /**
     * Получить или создать DerAccess для пользователя
     * По умолчанию пользователи имеют ограниченный доступ только к своему DER
     */
    public DerAccess getOrCreateDerAccess(int userId) {
        // Проверяем, есть ли уже запись
        Optional<DerAccess> existingAccess = derAccessRepository.findByUserId(userId);
        if (existingAccess.isPresent()) {
            return existingAccess.get();
        }

        // Если записи нет - создаем с ограниченным доступом к своему DER
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        Der userDer = user.getDer();
        
        if (userDer == null) {
            throw new RuntimeException("User " + userId + " has no DER assigned");
        }

        // Создаем новую запись с ограниченным доступом
        DerAccess derAccess = new DerAccess();
        derAccess.setUserId((long) userId);
        derAccess.setDerName(userDer);
        derAccess.setCanViewAll(false); // По умолчанию ограниченный доступ

        return derAccessRepository.save(derAccess);
    }

    /**
     * Проверить, может ли пользователь просматривать данные всех DER
     */
    public boolean canViewAllDers(int userId) {
        DerAccess derAccess = getOrCreateDerAccess(userId);
        return derAccess.isCanViewAll();
    }

    /**
     * Получить DER пользователя для ограничения доступа
     */
    public Der getUserDer(int userId) {
        DerAccess derAccess = getOrCreateDerAccess(userId);
        return derAccess.getDerName();
    }
}
