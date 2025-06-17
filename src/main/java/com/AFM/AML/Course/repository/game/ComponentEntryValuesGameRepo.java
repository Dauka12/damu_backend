package com.AFM.AML.Course.repository.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComponentEntryValuesGameRepo extends JpaRepository<com.AFM.AML.Course.models.game.ComponentEntryValuesGame, Integer> {
//    @Query(value = "SELECT v1_0.*" +
//            "FROM component_entry_values_game v1_0" +
//            " INNER JOIN component_entry_values_game_values cevv ON v1_0.component_entry_values_id = cevv.component_entry_values_component_entry_values_id" +
//            " WHERE cevv.property_name = 'img'", nativeQuery = true)
//    List<com.AFM.AML.Course.models.ComponentEntryValuesGame> getComponentEntriesByPhoto();
}
