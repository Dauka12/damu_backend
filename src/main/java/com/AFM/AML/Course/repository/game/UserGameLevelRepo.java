package com.AFM.AML.Course.repository.game;

import com.AFM.AML.Course.models.game.UserGame;
import com.AFM.AML.Course.models.game.UserGameLevel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserGameLevelRepo extends CrudRepository<UserGameLevel, Integer> {
    List<UserGameLevel> findAllByUserGame(UserGame userGame);
}
