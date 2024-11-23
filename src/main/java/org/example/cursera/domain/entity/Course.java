package org.example.cursera.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    private String description;

    private String companyName;

    private LocalDate createAt;

    private LocalDate updateAt;

    @ManyToMany
    @JoinTable(
            name = "course_subscribers",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> subscribers;

    private Long moderatorId;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Module> modules;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private MinioFile image;
}
