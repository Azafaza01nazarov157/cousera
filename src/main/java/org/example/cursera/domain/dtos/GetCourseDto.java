package org.example.cursera.domain.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetCourseDto {
    private Long id;

    private String name;

    private String description;

    private String companyName;

    private String createAt;

    private Long moderatorId;

    private List<ModuleDto> modules;

    private String image;
}
