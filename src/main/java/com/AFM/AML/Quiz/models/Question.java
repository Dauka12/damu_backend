package com.AFM.AML.Quiz.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Table
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int question_id;
    @Column(columnDefinition = "text")
    private String question_title;
    @Column(columnDefinition = "text")
    private String question_description;
    @Column(columnDefinition = "text")
    private String image;
    private String minio_image;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Quiz quiz;

    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    private List<Mcq_option> mcqOption;
    @OneToMany(mappedBy = "question", fetch = FetchType.EAGER)
    private List<MatchingPair> matchingPairs;
    @Override
    public String toString() {
        return "Question{" +
                "question_id=" + question_id +
                ", question_title='" + question_title + '\'' +
                ", question_description='" + question_description + '\'' +
                ", mcqOption=" + mcqOption +
                '}';
    }
}
