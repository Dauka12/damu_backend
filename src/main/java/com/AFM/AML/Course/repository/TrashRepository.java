package com.AFM.AML.Course.repository;

import com.AFM.AML.Course.models.Chapter;
import com.AFM.AML.Course.models.Trash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface TrashRepository extends JpaRepository<Trash, Integer> {

}
