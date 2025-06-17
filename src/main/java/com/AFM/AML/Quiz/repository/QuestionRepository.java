package com.AFM.AML.Quiz.repository;

import com.AFM.AML.Quiz.models.Question;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Integer> {
    @Query(value = "SELECT * FROM question where quiz_id = ?1", nativeQuery = true)
    List<Question> findQuestionByQuizId(int quiz_id);
    @Query(value = "SELECT count(*) FROM question where quiz_id = ?1", nativeQuery = true)
    int findCountOfQuestions(int quiz_id);

    @Query(value = "SELECT * FROM question where minio_image is not null", nativeQuery = true)
    List<Question> findByMinioImage();
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM question WHERE quiz_id = ?1", nativeQuery = true)
    void deleteByQuizId(int quiz_id);

}
