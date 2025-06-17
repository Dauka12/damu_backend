package com.AFM.AML.Course.repository.game;

import com.AFM.AML.Course.models.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepo extends JpaRepository<Game, Integer> {
}
