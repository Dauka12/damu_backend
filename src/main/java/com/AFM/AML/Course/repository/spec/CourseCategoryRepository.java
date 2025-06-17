package com.AFM.AML.Course.repository.spec;

import com.AFM.AML.Course.models.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Integer> {
    @Query("SELECT c FROM CourseCategory c WHERE c.category_name = :name")
    Optional<CourseCategory> findByCategoryName(@Param("name") String name);
    @Query("SELECT c FROM CourseCategory c WHERE c.isDeleted = false")
    List<CourseCategory> findAllNonDeleted();
}
