package com.AFM.AML.Quiz.repository;

import com.AFM.AML.Quiz.models.Mcq_option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Mcq_optionRepository extends JpaRepository<Mcq_option,Integer> {
    @Query(value = "SELECT * FROM mcq_option where question_id = ?1", nativeQuery = true)
    List<Mcq_option> findMcq_optionByQuestionID(int question_id);
    @Query(value = "SELECT CAST(COUNT(1) AS BIT) FROM mcq_option WHERE question_id = ?1 AND mcq_option_id = ?2 AND is_true = true", nativeQuery = true)
    Boolean checkCorrectAnswer(int question_id, int mcq_option_id);

    @Modifying
    @Query(value = "DELETE FROM mcq_option WHERE question_id = ?1", nativeQuery = true)
    void deleteByQuestionId(int question_id);
}
