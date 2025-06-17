package com.AFM.AML.statistics.service;

import com.AFM.AML.statistics.dto.*;
import com.AFM.AML.statistics.repository.StatisticsRepository;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class StatisticsService {

    private final StatisticsRepository repository;

    public StatisticsService(StatisticsRepository repository) {
        this.repository = repository;
    }

    public List<TypeStatisticsDto> getTypeStatistics(int userId, Integer month, Integer year) {
        List<Map<String, Object>> rows = repository.fetchTypeStatisticsRows(userId, month, year);


        Map<String, List<Map<String, Object>>> groupedByDerName = new HashMap<>();

        for (Map<String, Object> row : rows) {
            String derName = (String) row.get("der_name_rus");
            if (derName == null) {
                derName = "Без ДЭР";
            }
            groupedByDerName.computeIfAbsent(derName, k -> new ArrayList<>()).add(row);
        }

        List<TypeStatisticsDto> result = new ArrayList<>();

        for (String derName : groupedByDerName.keySet()) {
            List<Map<String, Object>> rowList = groupedByDerName.get(derName);

            int totalFinished = rowList.size();

            Map<Long, List<CourseFinishedInfoDto>> userCoursesMap = new HashMap<>();
            Map<Long, String> userFullNameMap = new HashMap<>();
            Map<Long, String> userDepartmentMap = new HashMap<>();

            for (Map<String, Object> r : rowList) {
                Long uId = ((Number) r.get("user_id")).longValue();
                String firstname = (String) r.get("firstname");
                String lastname = (String) r.get("lastname");
                String department_name = (String) r.get("department_name");
                String courseName = (String) r.get("course_name");

                // time_spent_days
                Integer timeSpentDays = null;
                if (r.get("time_spent_days") != null) {
                    timeSpentDays = ((Number) r.get("time_spent_days")).intValue();
                }

                userFullNameMap.putIfAbsent(uId, firstname + " " + lastname);
                userDepartmentMap.putIfAbsent(uId, department_name);

                userCoursesMap.computeIfAbsent(uId, x -> new ArrayList<>())
                        .add(new CourseFinishedInfoDto(courseName, timeSpentDays));
            }

            // 4. Формируем who_finished
            List<WhoFinishedDto> whoFinishedList = new ArrayList<>();
            for (Long uid : userCoursesMap.keySet()) {
                String fullName = userFullNameMap.get(uid);
                String department = userDepartmentMap.get(uid);
                List<CourseFinishedInfoDto> finishedList = userCoursesMap.get(uid);

                // В WhoFinishedDto (full_name, department, finished_courses)
                // учтём, что в конструкторе вы принимали (String full_name, String department, List<CourseFinishedInfoDto>)
                whoFinishedList.add(
                        new WhoFinishedDto(fullName, department, finishedList)
                );
            }

            // 5. Добавляем в общий результат
            //    ПЕРЕДАЁМ derName в поле type_of_member (так у вас DTO устроен)
            TypeStatisticsDto dto = new TypeStatisticsDto(
                    derName,
                    totalFinished,
                    whoFinishedList
            );
            result.add(dto);
        }

        return result;
    }

    public List<CourseStatisticsDto> getCourseStatistics(int userId, Integer month, Integer year) {
        List<Map<String, Object>> rows = repository.fetchCourseStatisticsRows(userId, month, year);

        // course_id -> список записей
        Map<Long, List<Map<String, Object>>> groupedByCourse = new HashMap<>();
        Map<Long, String> courseNames = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Long courseId = ((Number) row.get("course_id")).longValue();
            String courseName = (String) row.get("course_name");
            courseNames.put(courseId, courseName);

            groupedByCourse.computeIfAbsent(courseId, k -> new ArrayList<>()).add(row);
        }

        List<CourseStatisticsDto> result = new ArrayList<>();

        for (Long courseId : groupedByCourse.keySet()) {
            List<Map<String, Object>> rowList = groupedByCourse.get(courseId);

            // finished_count = rowList.size()
            List<WhoFinishedCourseDto> whoFinished = new ArrayList<>();
            for (Map<String, Object> r : rowList) {
                String fn = (String) r.get("firstname");
                String ln = (String) r.get("lastname");
                String department_name = (String) r.get("department_name");
                whoFinished.add(new WhoFinishedCourseDto(fn + " " + ln, department_name));
            }

            result.add(new CourseStatisticsDto(
                    courseId,
                    courseNames.get(courseId),
                    rowList.size(),
                    whoFinished
            ));
        }

        return result;
    }
}

