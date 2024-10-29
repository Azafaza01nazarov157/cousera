package org.example.cursera.domain.dtos.auth;

import lombok.Value;
import org.example.cursera.domain.enums.TokenType;

/**
 * DTO for {@link org.example.cursera.domain.entity.Token}
 */
@Value
public class TokenDto {
    Long id;
    String token;
    TokenType tokenType;
    boolean revoked;
    boolean expired;
}