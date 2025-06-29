package com.AFM.AML.Course.repository;


import com.AFM.AML.Course.models.ComponentEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentEntryRepo extends JpaRepository<ComponentEntry, Integer> {
    @Query(value = "SELECT * FROM component_entry ce where lesson_id = ?1", nativeQuery = true)
    List<ComponentEntry> findByLessonId(int id);

}
