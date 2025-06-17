package com.AFM.AML.Course.repository.game;

import com.AFM.AML.Course.models.game.UserGame;
import com.AFM.AML.User.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGameRepo extends JpaRepository<com.AFM.AML.Course.models.game.UserGame, Integer> {

    Optional<UserGame> findUserGameByUser(User user);

}
