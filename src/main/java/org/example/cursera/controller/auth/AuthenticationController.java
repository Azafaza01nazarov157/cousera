package org.example.cursera.controller.auth;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.auth.*;
import org.example.cursera.domain.dtos.auth.request.AuthenticationRequest;
import org.example.cursera.domain.dtos.auth.request.OtpRequest;
import org.example.cursera.domain.dtos.auth.request.RegisterRequest;
import org.example.cursera.domain.dtos.auth.response.AuthenticationResponse;
import org.example.cursera.domain.dtos.auth.response.RegisterResponse;
import org.example.cursera.service.auth.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @Operation(summary = "Register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/otp")
    @Operation(summary = "OtpStatus")
    public ResponseEntity<OtpStatus> otpStatus(@RequestBody OtpRequest request) {
        OtpStatus body = service.checkOtp(request);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "RefreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }



}
