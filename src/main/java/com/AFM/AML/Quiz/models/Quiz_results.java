package com.AFM.AML.Quiz.models;

import com.AFM.AML.User.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quiz_results {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int quiz_results_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private double points;
    private double all_points;
    private double score;
//    private Bo is_check;
}
