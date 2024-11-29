package org.example.cursera.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "minio_files")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinioFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size")
    private Long size;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User uploadedBy;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = true)
    private Lesson lesson;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @OneToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;
}
