package com.AFM.AML.User.repository;

import com.AFM.AML.User.models.DateDataDTO;
import com.AFM.AML.User.models.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<Log,Integer> {

    @Query(value = "SELECT TO_CHAR(l.date, 'YYYY-MM') as date, COUNT(l.log_id) as count, l.activity as name " +
            "FROM log l " +
            "WHERE l.activity = :activity " +
            "GROUP BY TO_CHAR(l.date, 'YYYY-MM'), l.activity ORDER BY TO_CHAR(l.date, 'YYYY-MM')", nativeQuery = true)
    List<Object[]> countLogsByMonthAndActivity(@Param("activity") String activity);

    @Query(value = "SELECT TO_CHAR(l.date, 'YYYY-MM') as date, COUNT(l.log_id) as count, l.activity as name " +
            "FROM log l " +
            "WHERE l.activity = :activity AND l.user_id = :userId " +
            "GROUP BY TO_CHAR(l.date, 'YYYY-MM'), l.activity ORDER BY TO_CHAR(l.date, 'YYYY-MM')", nativeQuery = true)
    List<Object[]> countLogsByMonthAndActivityByUser(@Param("activity") String activity,@Param("userId") Integer userId);
}
