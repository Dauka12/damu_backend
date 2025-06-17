package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.Chapter;
import com.AFM.AML.Course.models.Sub_chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Sub_chapterRepo extends JpaRepository<Sub_chapter, Integer> {
    @Query(value = "SELECT * FROM sub_chapter where chapter_id = ?1", nativeQuery = true)
    List<Sub_chapter> findAllSub_ChapterByCourseId(int chapter_id);
}
