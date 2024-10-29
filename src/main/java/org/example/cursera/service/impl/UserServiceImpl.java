package org.example.cursera.service.impl;


import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.UserDto;
import org.example.cursera.domain.dtos.auth.request.ChangePasswordRequest;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.exeption.NullPointException;
import org.example.cursera.mapper.UserMapper;
import org.example.cursera.service.user.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userDtoMapper;

    @Override
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (user != null) {
            throw new ForbiddenException(new ErrorDto("403", "Ошибка 403 (Forbidden/Доступ запрещён) возвращается клиенту сервером, когда доступ к указанному ресурсу ограничен."));
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDto getUser(Long userId) {
        if (userId == null) {
            throw new NullPointException(new ErrorDto("404", "User id null"));
        }
        User user = userRepository.findById(userId).orElseThrow();
        return userDtoMapper.toDto(user);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found by email")));
    }
}
