package org.example.cursera.domain.dtos;

import lombok.*;

import java.time.LocalDate;
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

    private LocalDate createAt;

    private Long moderatorId;

    private List<ModuleDto> modules;
}
