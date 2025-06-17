package com.AFM.AML.Course.models.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
public class SurveyAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer survey_answer_id;

    @Column(columnDefinition = "TEXT")
    private String survey_answer;

    private Boolean isTrue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_question_id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SurveyQuestion surveyQuestion;
}
