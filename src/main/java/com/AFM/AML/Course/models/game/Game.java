package com.AFM.AML.Course.models.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer game_id;
    private String name;
    private String description;

    private String image;
    @Nullable
    private String duration;
    @Nullable
    private Double rating;
    @Nullable
    private Boolean is_draft;
    @Nullable
    private Boolean is_deleted;


    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserGame> userGame;

    @OneToMany(mappedBy = "gameId")
    private List<Category> categorySet;

    @Nullable
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "game_id")
    private Survey survey;

}
