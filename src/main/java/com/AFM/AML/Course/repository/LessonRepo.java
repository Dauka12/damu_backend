package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.Lesson;
import com.AFM.AML.Course.models.Sub_chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepo extends JpaRepository<Lesson,Integer> {
    @Query(value = "SELECT * FROM lesson where module_id = ?1 and is_active = true", nativeQuery = true)
    List<Lesson> findByModuleIdAndActive(int module_id);


    @Query(value = "SELECT * FROM lesson where module_id = ?1", nativeQuery = true)
    List<Lesson> findByModuleId(int module_id);
}
