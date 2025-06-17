package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.CourseComments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseCommentsRepo extends JpaRepository<CourseComments,Integer> {
    @Query(value = "SELECT * FROM course_comments where comment is not null", nativeQuery = true)
    List<CourseComments> findByComment();
}
