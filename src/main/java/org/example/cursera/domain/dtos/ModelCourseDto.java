package org.example.cursera.domain.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelCourseDto {
    private Long id;

    private String name;

    private String description;

    private String companyName;

    private String  createAt;

    private Long moderatorId;
}
