package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseCategoryRepo extends JpaRepository<CourseCategory,Integer> {

}
