package com.AFM.AML.Course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.AFM.AML.Course.models.Course;
import com.AFM.AML.Course.models.UserCourse;
import com.AFM.AML.User.models.User;

import jakarta.transaction.Transactional;

public interface UserCourseRepository extends JpaRepository<UserCourse,Integer> {
    @Query(value = "update user_course set status = 'process' where user_id = :user_id and course_id = :course_id", nativeQuery = true)
    @Modifying
    @Transactional
    void findUserCourseByCourseAndUser(int user_id, int course_id);
    @Query(value = "update user_course set progress_percentage = 0 where user_id = :user_id and course_id = :course_id", nativeQuery = true)
    @Modifying
    @Transactional
    void resetCourse(int user_id, int course_id);

    @Query(value = "SELECT * from user_course where course_id = 8", nativeQuery = true)
    List<UserCourse> findBazovii();    List<UserCourse> findByUser(User user);
    
    @Query(value = "select * from user_course where user_id = ?1", nativeQuery = true)
    List<UserCourse> findByUserId(int userId);
    
    @Query(value = "select * from user_course where user_id = ?1 and course_id = ?2", nativeQuery = true)
    UserCourse findByUserAndCourseId(int user_id, int course_id);
    @Query(value = "select * from user_course where status = 'request'", nativeQuery = true)
    List<UserCourse> findByUserAndCourseIdRequest();

    @Query(value = "select course_id from user_course where status in ('process', 'finished') and user_id = ?1", nativeQuery = true)
    List<Integer> findByProcessAndFinished(int id);

    @Query(value = "select * from user_course where status in ('process', 'finished') and user_id = ?1 and course_id = ?2", nativeQuery = true)
    Optional<UserCourse> findPaymentInfo(int user, int course);
    @Query(value = "select count(*) from user_course where status = 'request' and course_id = ?1", nativeQuery = true)
    Integer getCountOfRequest(int courseId);

    @Query(value = "select count(*) from user_course where status = :target", nativeQuery = true)
    Integer getCountByStatus(@Param("target") String target);

    @Query(value = "select count(*) from user_course where status = :target and user_id = :userId", nativeQuery = true)
    Integer getCountByStatusAndUser(@Param("target") String target, @Param("userId") Integer userId);

    long countAllByCourseAndStatus(Course course, String status);
}
