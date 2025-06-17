package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.Chapter;
import com.AFM.AML.Course.models.Sub_chapter_materials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Sub_chapter_materials_repo extends JpaRepository<Sub_chapter_materials,Integer> {
    @Query(value = "SELECT * FROM sub_chapter_materials where sub_chapter_id = ?1", nativeQuery = true)
    List<Sub_chapter_materials> findAllSub_chapter_materialsBySubChapterId(int sub_chapter_id);
}
