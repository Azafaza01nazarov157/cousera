package org.example.cursera.domain.dtos;

import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetModuleDto {

    private Long id;

    private String name;

    private Integer lessons;
}
