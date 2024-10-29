package org.example.cursera.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tests")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String correctOption;
    
    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;
}
