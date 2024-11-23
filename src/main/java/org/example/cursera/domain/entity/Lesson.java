package org.example.cursera.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    private String level;

    @ManyToOne
    @JoinColumn(name = "module_id")
    private Module module;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Topic> topics;

    @ManyToMany
    @JoinTable(
            name = "completed_lessons",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> completedByUsers;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private MinioFile file;
}
