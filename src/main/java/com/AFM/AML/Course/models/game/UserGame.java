package com.AFM.AML.Course.models.game;

import com.AFM.AML.User.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "game_id"}))
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column    @Nullable
    private int score_sfm;

    @Column    @Nullable
    private Double start_quiz;
    @Column    @Nullable
    private Double end_quiz;
    @Column    @Nullable

    private Integer status;
    @Column    @Nullable

    private int avatar_id;
    @Column    @Nullable

    private Double percentage;

    @Nullable
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(mappedBy = "userGame")
    private Survey survey;

    @Nullable
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "userGame")
    @OrderBy("user_game_level_id ASC")
    private List<UserGameLevel> userGameLevel;



    @Override
    public String toString() {
        return "UserGame{" +
                "id=" + id +
                ", user=" + user +
                ", game=" + game +
                ", score_sfm=" + score_sfm +
                ", start_quiz=" + start_quiz +
                ", end_quiz=" + end_quiz +
                ", status='" + status + '\'' +
                ", avatar_id=" + avatar_id +
                ", percentage=" + percentage +
                ", survey=" + survey +
                '}';
    }
}
