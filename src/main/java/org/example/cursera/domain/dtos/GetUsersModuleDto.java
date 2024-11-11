package org.example.cursera.domain.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUsersModuleDto {
    private Long id;

    private String name;

    private String level;

    private List<LessonDto> lessons;

}
