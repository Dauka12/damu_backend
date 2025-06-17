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
public class Survey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer survey_id;

    private String survey_name;

    @Nullable
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Game game_id ;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "survey")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SurveyQuestion> questionList;

    @Nullable
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserGame userGame ;
}
