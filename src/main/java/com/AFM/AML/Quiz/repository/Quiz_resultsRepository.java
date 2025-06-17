package com.AFM.AML.Quiz.repository;

import com.AFM.AML.Quiz.models.Quiz_results;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface Quiz_resultsRepository extends JpaRepository<Quiz_results,Integer> {

    @Query(value = "SELECT distinct(count(*)) from quiz_results where user_id = ?1 and quiz_id = ?2 and score >=70",nativeQuery = true)
    int countOfFinishedTestsofUser(int user_id, int quiz_id);
    @Query(value = "SELECT EXISTS (SELECT 1 FROM quiz_results WHERE user_id = ?1 AND quiz_id = ?2 and score >= 70)", nativeQuery = true)
    boolean checkIsChecksAccept(int userId, int quizId);
    @Query(value = "SELECT EXISTS (SELECT 1 FROM quiz_results WHERE user_id = ?1 AND quiz_id = ?2 and score <= 70 GROUP BY user_id, quiz_id \n" +
            "HAVING COUNT(*) > 2)", nativeQuery = true)
    boolean checkIsChecksAcceptNot(int userId, int quizId);
    @Query(value = "SELECT EXISTS (SELECT 1 FROM quiz_results WHERE user_id = ?1 AND quiz_id = ?2 and score <= 70 GROUP BY user_id, quiz_id\n" +
            "    HAVING COUNT(*) > 2)", nativeQuery = true)
    boolean checkIsCheck(int userId, int quizId);
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM quiz_results WHERE quiz_results_id IN( select qr.quiz_results_id\n" +
            "            FROM quiz_results qr \n" +
            "            INNER JOIN quiz q ON q.quiz_id = qr.quiz_id \n" +
            "            INNER JOIN module m ON m.module_id = q.module_id \n" +
            "            INNER JOIN course c ON c.course_id = m.course_id  \n" +
            "            WHERE qr.user_id = ?1 AND c.course_id = ?2);", nativeQuery = true)

    void deleteAllByQuizIdAndUserID(int user_id, int course_id);
//    @Query(value = "SELECT * from quiz_results where MAX(score)", nativeQuery = true)

}
