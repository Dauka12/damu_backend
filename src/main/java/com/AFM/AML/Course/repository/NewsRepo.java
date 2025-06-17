package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface NewsRepo extends JpaRepository<News,Integer> {
    @Query(value = "select new com.AFM.AML.Course.models.News(n.id,n.image,n.kz_image,n.name,n.kz_name,n.date,n.eng_name,n.eng_image) from News n where n.type = ?1 ORDER BY n.date DESC")
    List<News> findAllByType(String type);

}
