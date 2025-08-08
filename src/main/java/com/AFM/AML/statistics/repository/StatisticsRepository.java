package com.AFM.AML.statistics.repository;

import com.AFM.AML.User.models.Der;
import com.AFM.AML.statistics.service.DerAccessService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class StatisticsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DerAccessService derAccessService;

    public StatisticsRepository(JdbcTemplate jdbcTemplate,
                                DerAccessService derAccessService) {
        this.jdbcTemplate = jdbcTemplate;
        this.derAccessService = derAccessService;
    }

    /**
     * Получаем данные для "type_statistics".
     */
    public List<Map<String, Object>> fetchTypeStatisticsRows(int userId, Integer month, Integer year) {
        // Получаем информацию о доступе пользователя
        boolean canViewAll = derAccessService.canViewAllDers(userId);
        Der userDer = derAccessService.getUserDer(userId);

        // Строим SQL
        StringBuilder sb = new StringBuilder();
        sb.append("""
          SELECT DISTINCT ON (u.user_id, c.course_id)
       u.user_id,
       u.firstname,
       u.lastname,
       d.name_rus AS der_name_rus,
       d.name_kaz AS der_name_kaz,
       c.course_id,
       c.course_name,
       dep.name_rus AS department_name,
       uc.payment_date,
       uc.date_certificate,
       (TO_DATE(uc.date_certificate, 'DD.MM.YYYY') - uc.payment_date::date) AS time_spent_days,
       qr.quiz_results_id,
       qr.quiz_id,
       qr.all_points,
       qr.points,
       qr.score
FROM _user u
JOIN der_list d ON d.id = u.der_id
JOIN user_course uc ON uc.user_id = u.user_id
LEFT JOIN department dep ON dep.id = u.department_id
JOIN course c ON c.course_id = uc.course_id
LEFT JOIN module m ON m.course_id = c.course_id
LEFT JOIN quiz q ON q.module_id = m.id
INNER JOIN quiz_results qr ON qr.quiz_id = q.quiz_id AND qr.user_id = u.user_id
WHERE uc.status = 'finished'
ORDER BY u.user_id, c.course_id, uc.payment_date;

        """);

        // Если canViewAll = false, фильтруем по der_id пользователя
        if (!canViewAll && userDer != null) {
            sb.append(" AND u.der_id = ").append(userDer.getId());
        }

        // Фильтр по month/year (опциональный)
        if (month != null && year != null) {
            sb.append(" AND EXTRACT(MONTH FROM uc.payment_date) = ").append(month);
            sb.append(" AND EXTRACT(YEAR FROM uc.payment_date) = ").append(year);
        }

        // Выполняем запрос и возвращаем результат
        return jdbcTemplate.queryForList(sb.toString());
    }

    /**
     * Получаем данные для "course_statistics".
     */
public List<Map<String, Object>> fetchCourseStatisticsRows(int userId, Integer month, Integer year) {
    // Получаем информацию о доступе пользователя
    boolean canViewAll = derAccessService.canViewAllDers(userId);
    Der userDer = derAccessService.getUserDer(userId);

    // Строим SQL
    StringBuilder sb = new StringBuilder();
    sb.append("""
        SELECT DISTINCT
          u.user_id,
          u.firstname,
          u.lastname,
          dep.name_rus AS department_name,
          d.name_rus AS der_name_rus,
          d.name_kaz AS der_name_kaz,
          c.course_id,
          c.course_name,
          uc.payment_date,
          uc.date_certificate,
          (TO_DATE(uc.date_certificate, 'DD.MM.YYYY') - uc.payment_date::date) AS time_spent_days
        FROM user_course uc
        JOIN course c ON c.course_id = uc.course_id
        JOIN _user u ON u.user_id = uc.user_id
        LEFT JOIN department dep ON dep.id = u.department_id
        JOIN der_list d ON d.id = u.der_id
        WHERE uc.status = 'finished'
    """);

    // Если canViewAll = false, фильтруем по der_id пользователя
    if (!canViewAll && userDer != null) {
        sb.append(" AND u.der_id = ").append(userDer.getId());
    }

    // Фильтр по month/year
    if (month != null && year != null) {
        sb.append(" AND EXTRACT(MONTH FROM uc.payment_date) = ").append(month);
        sb.append(" AND EXTRACT(YEAR FROM uc.payment_date) = ").append(year);
    }

    // Добавляем ORDER BY для упорядочивания
    sb.append(" ORDER BY u.user_id, c.course_id");

    // Выполняем и возвращаем результат
    return jdbcTemplate.queryForList(sb.toString());
}

}
