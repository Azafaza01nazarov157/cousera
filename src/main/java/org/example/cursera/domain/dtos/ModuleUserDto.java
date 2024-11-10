package org.example.cursera.domain.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleUserDto {
    private Long id;

    private String name;

    private String description;

    private String level;

    private String courseName;
}
