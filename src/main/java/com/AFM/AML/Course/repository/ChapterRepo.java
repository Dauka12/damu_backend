package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepo extends JpaRepository<Chapter, Integer>{
    @Query(value = "SELECT * FROM chapter where course_id = ?1 AND is_active = true", nativeQuery = true)
    List<Chapter> findAllChapterByCourseId(int course_id);


}
