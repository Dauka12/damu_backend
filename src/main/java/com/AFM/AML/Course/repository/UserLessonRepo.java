package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.UserLessonCheck;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserLessonRepo extends JpaRepository<UserLessonCheck, Integer> {
    @Query(value = "SELECT ulcc.* \n" +
            "             FROM user_lesson_check ulcc \n" +
            "            INNER JOIN lesson l ON ulcc.lesson_id = l.lesson_id \n" +
            "            INNER JOIN module m ON m.module_id = l.module_id \n" +
            "            WHERE m.course_id = ?2 AND ulcc.user_id = ?1 and l.is_active = true", nativeQuery = true)
    List<UserLessonCheck> findCheckedByCourseId(int user_id, int course_id);
    @Modifying
    @Transactional
    @Query(value = "UPDATE user_lesson_check set checked = true where user_id = :user_id and lesson_id = :lesson_id and checked = false", nativeQuery = true)
    void checked(int user_id, int lesson_id);
    @Modifying
    @Transactional
    @Query(value = "UPDATE user_lesson_check ulc SET checked = false " +
            "FROM lesson l " +
            "INNER JOIN module m ON m.module_id = l.module_id " +
            "INNER JOIN course c ON m.course_id = c.course_id " +
            "WHERE ulc.lesson_id = l.lesson_id " +
            "AND ulc.user_id = ?2 " +
            "AND ulc.checked = true " +
            "AND c.course_id = ?1", nativeQuery = true)
    void unchecked(int course_id, int user_id);

    @Query(value = "SELECT ulc.* FROM user_lesson_check ulc \n" +
            "                INNER JOIN lesson l ON ulc.lesson_id = l.lesson_id \n" +
            "                WHERE l.module_id = ?1",nativeQuery = true)
    List<UserLessonCheck> getUserLessonCheckByModuleId(int module_id);
    @Query(value = "SELECT count(*) FROM user_lesson_check ulc " +
            "INNER JOIN lesson l ON ulc.lesson_id = l.lesson_id " +
            "WHERE ulc.user_id = ?1 AND ulc.checked = 'true' AND l.module_id = ?2 and l.is_active = true",
            nativeQuery = true)
    int countOfCheckedLessons(int user_id, int module_id);
    @Query(value = "SELECT * from user_lesson_check", nativeQuery = true)
    List<UserLessonCheck> findAllWithDetails();


}
