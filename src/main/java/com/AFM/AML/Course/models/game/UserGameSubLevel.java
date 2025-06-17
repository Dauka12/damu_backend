package com.AFM.AML.Course.models.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class UserGameSubLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userGameSubLevelId;

    private Integer quantity;

    private Double percentage=(double)0;

    @ElementCollection
    private List<Double> answers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_game_level_id")
    @JsonIgnore
    private UserGameLevel userGameLevel;


}
