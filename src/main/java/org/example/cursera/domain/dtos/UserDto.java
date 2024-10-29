package org.example.cursera.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.cursera.constants.Constants;
import org.example.cursera.domain.dtos.auth.TokenDto;
import org.example.cursera.domain.enums.Role;

import java.util.List;

/**
 * DTO for {@link org.example.cursera.domain.entity.User}
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
}