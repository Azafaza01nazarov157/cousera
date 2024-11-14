package org.example.cursera.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestSubmissionDto {
    private Long testId;
    private String selectedOption;
}