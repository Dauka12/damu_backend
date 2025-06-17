package com.AFM.AML.Course.models.game;

import com.AFM.AML.Course.models.ComponentEntryValues;
import com.AFM.AML.Course.models.game.Level;
import com.AFM.AML.Course.models.game.SubLevel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "component_entry_game")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentEntryGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer component_entry_game_id;
    private String componentName;

    @ManyToOne
    @JoinColumn(name = "subLevel_id")
    @JsonIgnore
    private SubLevel sublevel;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "component_entry_values_game_id")
    private ComponentEntryValuesGame values;
}

