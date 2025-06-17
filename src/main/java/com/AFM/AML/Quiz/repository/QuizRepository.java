package com.AFM.AML.Quiz.repository;

import com.AFM.AML.Quiz.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz,Integer> {

    @Query(value = "SELECT * FROM quiz where module_id = ?1", nativeQuery = true)
    Optional<Quiz> findQuizByChapterID(int module_id);
}
