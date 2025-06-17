package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.Chapter;
import com.AFM.AML.Course.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepo extends JpaRepository<Module,Integer> {
    @Query(value = "SELECT * FROM module where course_id = ?1", nativeQuery = true)
    List<Module> findByCourseId(int course_id);

    @Query(value = "SELECT * FROM module where course_id = ?1 AND is_active = true order by module_id", nativeQuery = true)
    List<Module> findByCourseIdAndActive(int course_id);
}
