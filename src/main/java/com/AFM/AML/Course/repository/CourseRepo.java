package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.Course;
import com.AFM.AML.Course.models.DTOs.CourseWithoutModuleDTO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepo extends JpaRepository<Course,Integer> {
    Page<Course> findAll(Pageable pageable);

    @Query(value = "select * from course where deleted = false", nativeQuery = true)
    List<Course> findByDeleted();

    @Query(value = "select new com.AFM.AML.Course.models.DTOs.CourseWithoutModuleDTO(c.course_id, c.course_name, c.course_image, c.course_price, c.course_for_member_of_the_system, c.draft, c.deleted) from Course c where c.deleted = false", nativeQuery = false)
    List<CourseWithoutModuleDTO> findByDeletedWithoutModule();

//    @Query(value = "select * from course where deleted = false", nativeQuery = true)
//    List<Course> findByDeleted();

    @Query(value = "select c.*, (select count(*) from user_course where user_course.course_id = c.course_id and status = 'finished') as completed_users from course c where deleted = false and draft = false", nativeQuery = true)
    List<Course> findAvailable();

    @Query(value = "SELECT c.* FROM course c INNER JOIN module m ON m.module_id = c.course_id INNER JOIN quiz l ON l.quiz_id = m.module_id WHERE l.quiz_id = ?1", nativeQuery = true)
    Course findByQuizId(int quiz_id);

    @Query(value = "SELECT c.*, 0 as completed_users FROM course c INNER JOIN module m ON m.course_id = c.course_id INNER JOIN lesson l ON l.module_id = m.module_id WHERE l.lesson_id = ?1", nativeQuery = true)
    Course findByLessonId(int lesson_id);
//    @Query(value = "select count(*) from course c where c.type_of_study = 'онлайн' and c.course_id = ?1", nativeQuery = true)
//    Integer getCountOfRequest(int courseId);
    @Modifying
    @Query("UPDATE Course c SET c.views = c.views + 1 WHERE c.course_id = :courseId")
    void incrementViews(@Param("courseId") int courseId);
}
