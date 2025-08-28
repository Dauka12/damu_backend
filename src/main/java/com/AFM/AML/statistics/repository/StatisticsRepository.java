package com.AFM.AML.statistics.repository;

import com.AFM.AML.User.models.Der;
import com.AFM.AML.statistics.service.DerAccessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Slf4j
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
        boolean canViewAll = derAccessService.canViewAllDers(userId);
        Der userDer = derAccessService.getUserDer(userId);

        StringBuilder sql = new StringBuilder("""
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
               (TO_DATE(NULLIF(uc.date_certificate, ''), 'DD.MM.YYYY') - uc.payment_date::date) AS time_spent_days,
               qr.quiz_results_id,
               qr.quiz_id,
               qr.all_points,
               qr.points,
               qr.score
        FROM _user u
        JOIN der_list d           ON d.id = u.der_id
        JOIN user_course uc       ON uc.user_id = u.user_id
        LEFT JOIN department dep  ON dep.id = u.department_id
        JOIN course c             ON c.course_id = uc.course_id
        LEFT JOIN module m        ON m.course_id = c.course_id
        LEFT JOIN quiz q          ON q.module_id = m.id
        INNER JOIN quiz_results qr ON qr.quiz_id = q.quiz_id AND qr.user_id = u.user_id
        WHERE uc.status = 'finished'
    """);

        List<Object> args = new ArrayList<>();

        if (!canViewAll && userDer != null) {
            sql.append(" AND u.der_id = ? ");
            args.add(userDer.getId());
        }

        if (month != null && year != null) {
            sql.append(" AND EXTRACT(MONTH FROM uc.payment_date) = ? ");
            sql.append(" AND EXTRACT(YEAR  FROM uc.payment_date) = ? ");
            args.add(month);
            args.add(year);
        }


        sql.append(" ORDER BY u.user_id, c.course_id, uc.payment_date DESC");


        return jdbcTemplate.queryForList(sql.toString(), args.toArray());
    }

    /**
     * Получаем данные для "course_statistics".
     */
    public List<Map<String, Object>> fetchCourseStatisticsRows(
            int userId, Integer month, Integer year) {

        boolean canViewAll = derAccessService.canViewAllDers(userId);
        Der userDer = derAccessService.getUserDer(userId);

        StringBuilder sql = new StringBuilder("""
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
          (TO_DATE(NULLIF(uc.date_certificate, ''), 'DD.MM.YYYY') - uc.payment_date::date) AS time_spent_days
        FROM user_course uc
        JOIN course c ON c.course_id = uc.course_id
        JOIN _user u ON u.user_id = uc.user_id
        LEFT JOIN department dep ON dep.id = u.department_id
        JOIN der_list d ON d.id = u.der_id
        WHERE uc.status = 'finished'
    """);

        List<Object> args = new ArrayList<>();

        if (!canViewAll && userDer != null) {
            sql.append(" AND u.der_id = ? ");
            args.add(userDer.getId());
        }

        if (month != null && year != null) {
            sql.append(" AND EXTRACT(MONTH FROM uc.payment_date) = ? ");
            sql.append(" AND EXTRACT(YEAR FROM uc.payment_date) = ? ");
            args.add(month);
            args.add(year);
        }

        sql.append(" ORDER BY u.user_id, c.course_id");

        return jdbcTemplate.queryForList(sql.toString(), args.toArray());
    }


}
