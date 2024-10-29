package org.example.cursera.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.example.cursera.domain.entity.Course;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"course"})
public class ModuleDto {

    private Long id;

    private String name;

    private List<ModelCourseDto> course;

    private Integer lessons;
}
