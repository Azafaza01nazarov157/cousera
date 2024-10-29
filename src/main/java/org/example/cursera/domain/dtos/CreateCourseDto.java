package org.example.cursera.domain.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateCourseDto {
    private String name;
    private String description;
    private String companyName;
    private Long moderatorId;
}
