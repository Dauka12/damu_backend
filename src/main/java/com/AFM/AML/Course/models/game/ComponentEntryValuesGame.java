package com.AFM.AML.Course.models.game;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Entity
@Table(name = "component_entry_values_game")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComponentEntryValuesGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer component_entry_values_game_id;

    @ElementCollection
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value", columnDefinition = "TEXT")
    private Map<String, String> values;
}
