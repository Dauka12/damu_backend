package com.AFM.AML.Course.models.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Data
public class UserGameLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_game_level_id;

    private Double percentage=(double)0;

    private Boolean isPassed=false;

    private Boolean isActive=false;

    @Nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserGame userGame ;

    @OneToMany(mappedBy = "userGameLevel")
    @OrderBy("userGameSubLevelId ASC")
    private List<UserGameSubLevel> userGameSubLevelList;

}
