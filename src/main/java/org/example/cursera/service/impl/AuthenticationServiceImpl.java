package org.example.cursera.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.auth.OtpStatus;
import org.example.cursera.domain.dtos.auth.request.AuthenticationRequest;
import org.example.cursera.domain.dtos.auth.request.OtpRequest;
import org.example.cursera.domain.dtos.auth.request.RegisterRequest;
import org.example.cursera.domain.dtos.auth.response.AuthenticationResponse;
import org.example.cursera.domain.dtos.auth.response.RegisterResponse;
import org.example.cursera.domain.entity.Token;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.enums.TokenType;
import org.example.cursera.domain.repository.TokenRepository;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.securety.JwtServiceImpl;
import org.example.cursera.service.auth.AuthenticationService;
import org.example.cursera.service.gmail.MailSenderService;
import org.example.cursera.service.gmail.OtpService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final MailSenderService mailService;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        String otp = otpService.generateOtp();

        mailService.sendNewMail(request.getEmail(), "Ваш OTP-код: ", otp);

        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .otp(otp)
                .checkOtp(false)
                .attempt(0)
                .active(true)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User save = repository.save(user);

        return RegisterResponse.builder()
                .text("Пользователь успешно сохранен. Пройдите аутентификацию с OTP.")
                .email(save.getEmail())
                .build();
    }


    @Override
    @Transactional
    public OtpStatus checkOtp(OtpRequest otpRequest) {
        var user = repository.findByEmail(otpRequest.getEmail())
                .orElseThrow();
        if (user.getOtp().equals(otpRequest.getOtp())) {
            user.setCheckOtp(true);
            repository.save(user);
            return OtpStatus.builder().status(true).build();
        } else {
            user.setAttempt(user.getAttempt() + 1);
            if (user.getAttempt() >= 2) {
                user.setActive(false);
            }
            repository.save(user);
            return OtpStatus.builder().status(false).build();
        }
    }

    @SneakyThrows
    @Override
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            var user = repository.findByEmail(request.getEmail()).orElseThrow();

            if (!user.getActive()) {
                throw new Exception("Ваш user блокирован");
            }
            if (!user.getCheckOtp()) {
                throw new Exception("Проверка OTP ещё не пройдена");
            }

            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (Exception e) {
            log.error("Ошибка аутентификации: {}", e.getMessage(), e);
            throw e; // перехват исключения для логирования, затем проброс его наверх
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false).revoked(false).build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail).orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
