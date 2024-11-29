package org.example.cursera.domain.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    @JsonIgnore
    private MultipartFile image;
}
