package com.AFM.AML.Course.repository.game;


import com.AFM.AML.Course.models.game.Survey;
import com.AFM.AML.Course.models.game.UserGame;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepo extends JpaRepository<Survey, Integer> {
    Optional<Survey> findSurveyByUserGame(UserGame userGame);
}
