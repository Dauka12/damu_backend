package com.AFM.AML.Course.repository.game;

import com.AFM.AML.Course.models.ComponentEntry;
import com.AFM.AML.Course.models.game.ComponentEntryGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComponentEntryGameRepo extends JpaRepository<ComponentEntryGame, Integer> {
//    @Query(value = "SELECT * FROM component_entry_game ce where level_id = ?1", nativeQuery = true)
//    List<ComponentEntryGame> findByLessonId(int id);
}
