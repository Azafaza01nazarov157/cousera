package org.example.cursera.service.auth;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.cursera.domain.dtos.auth.*;
import org.example.cursera.domain.dtos.auth.request.AuthenticationRequest;
import org.example.cursera.domain.dtos.auth.request.OtpRequest;
import org.example.cursera.domain.dtos.auth.request.RegisterRequest;
import org.example.cursera.domain.dtos.auth.response.AuthenticationResponse;
import org.example.cursera.domain.dtos.auth.response.RegisterResponse;

import java.io.IOException;

public interface AuthenticationService {
    RegisterResponse register(RegisterRequest request);

    OtpStatus checkOtp(OtpRequest otpRequest);

    AuthenticationResponse authenticate(AuthenticationRequest request);

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;
}
