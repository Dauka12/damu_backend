package com.AFM.AML.statistics.repository;

import com.AFM.AML.User.models.Der;
import com.AFM.AML.statistics.model.DerAccess;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StatisticsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DerAccessRepository derAccessRepository;

    public StatisticsRepository(JdbcTemplate jdbcTemplate,
                                DerAccessRepository derAccessRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.derAccessRepository = derAccessRepository;
    }

    /**
     * Получаем данные для "type_statistics".
     */
    public List<Map<String, Object>> fetchTypeStatisticsRows(int userId, Integer month, Integer year) {
        // 1) Находим запись в der_access
        Optional<DerAccess> opt = derAccessRepository.findByUserId(userId);
        if (opt.isEmpty()) {
            // Если запись не найдена, можно вернуть пустой список или кинуть исключение
            return Collections.emptyList();
        }

        // 2) Извлекаем нужные поля
        DerAccess da = opt.get();
        boolean canViewAll = da.isCanViewAll();
        Der userDer = da.getDerName(); // Это объект Der (содержит id, name_rus, name_kaz и т.п.)

        // 3) Строим SQL
        StringBuilder sb = new StringBuilder();
        sb.append("""
            SELECT
              u.user_id,
              u.firstname,
              u.lastname,
              d.name_rus AS der_name_rus,
              d.name_kaz AS der_name_kaz,
              c.course_id,
              c.course_name,
              dep.name_rus as department_name,
              uc.payment_date,
              uc.date_certificate,
              (TO_DATE(uc.date_certificate, 'DD.MM.YYYY') - uc.payment_date::date) AS time_spent_days
            FROM _user u
            JOIN der_list d ON d.id = u.der_id
            JOIN user_course uc ON uc.user_id = u.user_id
            left JOIN department dep ON dep.id = u.department_id
            JOIN course c ON c.course_id = uc.course_id
            WHERE uc.status = 'finished'
        """);

        // Если canViewAll = false, нужно фильтровать по тому der_id, который есть у пользователя
        if (!canViewAll && userDer != null) {
            sb.append(" AND u.der_id = ").append(userDer.getId());
        }

        // Фильтр по month/year (опциональный)
        if (month != null && year != null) {
            sb.append(" AND EXTRACT(MONTH FROM uc.payment_date) = ").append(month);
            sb.append(" AND EXTRACT(YEAR FROM uc.payment_date) = ").append(year);
        }

        // 4) Выполняем запрос и возвращаем результат
        return jdbcTemplate.queryForList(sb.toString());
    }

    /**
     * Получаем данные для "course_statistics".
     */
    public List<Map<String, Object>> fetchCourseStatisticsRows(int userId, Integer month, Integer year) {
        // 1) Находим запись в der_access
        Optional<DerAccess> opt = derAccessRepository.findByUserId(userId);
        if (opt.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) Извлекаем нужные поля
        DerAccess da = opt.get();
        boolean canViewAll = da.isCanViewAll();
        Der userDer = da.getDerName(); // указываем, что der - объект

        // 3) Строим SQL
        StringBuilder sb = new StringBuilder();
        sb.append("""
            SELECT
              c.course_id,
              c.course_name,
              u.user_id,
              u.firstname,
              u.lastname,
              dep.name_rus as department_name,
              d.name_rus AS der_name_rus,
              d.name_kaz AS der_name_kaz,
              uc.payment_date,
              uc.date_certificate,
              (TO_DATE(uc.date_certificate, 'DD.MM.YYYY') - uc.payment_date::date) AS time_spent_days
            FROM user_course uc
            JOIN course c ON c.course_id = uc.course_id
            JOIN _user u ON u.user_id = uc.user_id
            left JOIN department dep ON dep.id = u.department_id
            JOIN der_list d ON d.id = u.der_id
            WHERE uc.status = 'finished'
        """);

        // Если canViewAll = false, фильтруем по u.der_id
        if (!canViewAll && userDer != null) {
            sb.append(" AND u.der_id = ").append(userDer.getId());
        }

        // Фильтр по month/year
        if (month != null && year != null) {
            sb.append(" AND EXTRACT(MONTH FROM uc.payment_date) = ").append(month);
            sb.append(" AND EXTRACT(YEAR FROM uc.payment_date) = ").append(year);
        }

        // 4) Выполняем и возвращаем
        return jdbcTemplate.queryForList(sb.toString());
    }

}
