package org.example.cursera.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersDto {
    private Long id;
    private String username;
    private String email;
    private String otp;
    private Integer attempt;
    @JsonProperty("check_otp")
    private Boolean checkOtp;
    private Boolean active;
    private String role;

}
