package org.example.cursera.domain.dtos.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    @JsonProperty("text")
    private String text;
    @JsonProperty("email")
    private String email;
}