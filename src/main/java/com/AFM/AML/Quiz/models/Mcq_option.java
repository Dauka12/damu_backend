package com.AFM.AML.Quiz.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Table
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mcq_option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mcq_option_id;
    @Column(columnDefinition = "text")
    private String mcq_option_title;
    private Boolean is_true;
    @ManyToOne
    @JoinColumn(name = "question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Question question;

    @Override
    public String toString() {
        return "Mcq_option{" +
                "mcq_option_id=" + mcq_option_id +
                ", mcq_option_title='" + mcq_option_title + '\'' +
                ", is_true=" + is_true +
                '}';
    }
}
