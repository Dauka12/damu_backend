package com.AFM.AML.Course.repository.game;

import com.AFM.AML.Course.models.Lesson;
import com.AFM.AML.Course.models.game.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

public interface LevelRepo extends JpaRepository<Level, Integer> {
}
