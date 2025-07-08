package com.AFM.AML.statistics.controller;

import com.AFM.AML.User.models.Der;
import com.AFM.AML.User.repository.UserRepository;
import com.AFM.AML.statistics.model.DerAccess;
import com.AFM.AML.statistics.repository.DerAccessRepository;
import com.AFM.AML.statistics.service.DerAccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/der-access")
public class DerAccessController {

    private final DerAccessRepository derAccessRepository;
    private final DerAccessService derAccessService;

    public DerAccessController(DerAccessRepository derAccessRepository, 
                              UserRepository userRepository,
                              DerAccessService derAccessService) {
        this.derAccessRepository = derAccessRepository;
        this.derAccessService = derAccessService;
    }

    /**
     * Получить информацию о доступе пользователя
     * GET /api/admin/der-access/info?userId=660
     */
    @GetMapping("/info")
    public ResponseEntity<?> getDerAccessInfo(@RequestParam int userId) {
        try {
            boolean canViewAll = derAccessService.canViewAllDers(userId);
            Der userDer = derAccessService.getUserDer(userId);
            
            return ResponseEntity.ok("User " + userId + " has access to DER: " + 
                                   userDer.getName_rus() + 
                                   " (canViewAll=" + canViewAll + ")");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Предоставить полный доступ пользователю
     * POST /api/admin/der-access/grant-full-access?userId=660
     */
    @PostMapping("/grant-full-access")
    public ResponseEntity<String> grantFullAccess(@RequestParam int userId) {
        try {
            // Получаем или создаем запись DerAccess
            DerAccess derAccess = derAccessService.getOrCreateDerAccess(userId);
            
            // Предоставляем полный доступ
            derAccess.setCanViewAll(true);
            derAccessRepository.save(derAccess);
            
            return ResponseEntity.ok("Full access granted to user " + userId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Ограничить доступ пользователя только его DER
     * POST /api/admin/der-access/restrict-access?userId=660
     */
    @PostMapping("/restrict-access")
    public ResponseEntity<String> restrictAccess(@RequestParam int userId) {
        try {
            // Получаем или создаем запись DerAccess
            DerAccess derAccess = derAccessService.getOrCreateDerAccess(userId);
            
            // Ограничиваем доступ
            derAccess.setCanViewAll(false);
            derAccessRepository.save(derAccess);
            
            return ResponseEntity.ok("Access restricted to user's DER for user " + userId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
