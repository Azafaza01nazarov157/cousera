package org.example.cursera.domain.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriberDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Long requestId;
}