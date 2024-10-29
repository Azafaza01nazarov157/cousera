package org.example.cursera.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "test_results")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int score;

    private double percentage;

    @Column(name = "is_correct")
    private boolean isCorrect;
}
