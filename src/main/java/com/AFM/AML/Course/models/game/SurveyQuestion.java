package com.AFM.AML.Course.models.game;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Data
public class SurveyQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer survey_question_id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Survey survey;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "surveyQuestion")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<SurveyAnswer> answersList;
}
