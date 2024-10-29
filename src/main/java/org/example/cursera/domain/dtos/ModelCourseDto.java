package org.example.cursera.domain.dtos;

import lombok.*;

import java.time.LocalDate;

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

    private LocalDate createAt;

    private Long moderatorId;
}
