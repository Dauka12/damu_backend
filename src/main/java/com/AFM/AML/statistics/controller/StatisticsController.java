package com.AFM.AML.statistics.controller;

import com.AFM.AML.statistics.dto.CourseStatisticsDto;
import com.AFM.AML.statistics.dto.TypeStatisticsDto;
import com.AFM.AML.statistics.repository.DerAccessRepository;
import com.AFM.AML.statistics.service.StatisticsService;
import com.AFM.AML.User.service.CurrentUserService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final CurrentUserService currentUserService;
    private final DerAccessRepository derAccessRepository;

    public StatisticsController(StatisticsService statisticsService,
                                CurrentUserService currentUserService,
                                DerAccessRepository derAccessRepository) {
        this.statisticsService = statisticsService;
        this.currentUserService = currentUserService;
        this.derAccessRepository = derAccessRepository;
    }

    // Пример: GET /api/statistics/type_statistics?month=5&year=2024
    @GetMapping("/type_statistics")
    public List<TypeStatisticsDto> getTypeStatistics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        int userId = currentUserService.getCurrentUserId();
        return statisticsService.getTypeStatistics(userId, month, year);
    }

    // Пример: GET /api/statistics/course_statistics?month=5&year=2024
    @GetMapping("/course_statistics")
    public List<CourseStatisticsDto> getCourseStatistics(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        int userId = currentUserService.getCurrentUserId();
        return statisticsService.getCourseStatistics(userId, month, year);
    }

    @GetMapping("/der_access_info")
    public Map<String, Object> getDerAccessInfo() {
        int userId = currentUserService.getCurrentUserId();
        var daOpt = derAccessRepository.findByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        if (daOpt.isPresent()) {
            response.put("canViewAll", daOpt.get().isCanViewAll());
            response.put("derName", daOpt.get().getDerName());
        } else {
            // Если записи нет, как-то обрабатываем
            response.put("canViewAll", false);
            response.put("derName", "Нет записи в derAccess");
        }
        return response;
    }

}
