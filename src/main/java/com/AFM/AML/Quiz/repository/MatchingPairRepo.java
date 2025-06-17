package com.AFM.AML.Quiz.repository;

import com.AFM.AML.Quiz.models.MatchingPair;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MatchingPairRepo extends JpaRepository<MatchingPair,Integer> {
    @Query(value = "SELECT CAST(COUNT(1) AS BIT) FROM matching_pair WHERE question_id = ?1 AND left_part = ?2 AND right_part = ?3", nativeQuery = true)
    Boolean checkCorrectAnswerMatchingPair(int question_id, String left_part, String right_part);

    @Modifying
    @Query(value = "DELETE FROM matching_pair WHERE question_id = ?1", nativeQuery = true)
    void deleteByQuestionId(int question_id);
}
