package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.cursera.domain.dtos.UsersDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.enums.Role;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.user.AdminService;
import org.example.cursera.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public List<UsersDto> findAllUsers(Long userId) {
        val user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));

        if (Role.ADMIN.equals(user.getRole())) {
            val users = userRepository.findAll();
            val filteredUsers = users.stream()
                    .filter(u -> Role.USER.equals(u.getRole()) && Boolean.TRUE.equals(u.getCheckOtp()))
                    .toList();
            return filteredUsers.stream()
                    .map(this::convertToUsersDto)
                    .toList();
        } else {
            throw new ForbiddenException(new ErrorDto("403", "Access denied"));
        }
    }


    @Override
    public List<UsersDto> findAllMODERATOR(Long userId) {
        val user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));

        if (Role.ADMIN.equals(user.getRole())) {
            List<User> users = userRepository.findAll();
            List<User> moderators = users.stream()
                    .filter(u -> Role.MODERATOR.equals(u.getRole()))
                    .toList();

            return moderators.stream()
                    .map(this::convertToUsersDto)
                    .toList();
        } else {
            throw new ForbiddenException(new ErrorDto("403", "Access denied"));
        }
    }


    @Override
    public void createModerator(Long adminId, String moderatorEmail) {
        val admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));
        if (!Role.ADMIN.equals(admin.getRole())) {
            throw new ForbiddenException(new ErrorDto("403", "Access denied"));
        }
        final User user = userService.findByEmail(moderatorEmail);
        user.setRole(Role.MODERATOR);
        userRepository.save(user);
    }

    public void deleteUser(Long adminId, Long userId) {
        val admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));
        if (!Role.ADMIN.equals(admin.getRole())) {
            throw new ForbiddenException(new ErrorDto("403", "Access denied"));
        }
        userRepository.deleteById(userId);
        log.info("User with ID {} has been deleted by Admin with ID {}", userId, adminId);
    }

    public void updateUserRole(Long adminId, Long userId, String newRole) {
        val admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));
        if (!Role.ADMIN.equals(admin.getRole())) {
            throw new ForbiddenException(new ErrorDto("403", "Access denied"));
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));
        Role updatedRole = Role.valueOf(newRole.toUpperCase());
        user.setRole(updatedRole);
        userRepository.save(user);
        log.info("User with ID {} role updated to {} by Admin with ID {}", userId, newRole, adminId);
    }

    public List<UsersDto> findAllModerators(Long adminId) {
        val admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));

        if (!Role.ADMIN.equals(admin.getRole())) {
            throw new ForbiddenException(new ErrorDto("403", "Access denied"));
        }

        return userRepository.findAll().stream()
                .filter(user -> Role.MODERATOR.equals(user.getRole()))
                .map(this::convertToUsersDto)
                .collect(Collectors.toList());
    }

    public List<UsersDto> findAllActiveUsers(Long adminId) {
        val admin = userRepository.findById(adminId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));

        if (!Role.ADMIN.equals(admin.getRole())) {
            throw new ForbiddenException(new ErrorDto("403", "Access denied"));
        }

        return userRepository.findAll().stream()
                .filter(User::isActive)
                .map(this::convertToUsersDto)
                .collect(Collectors.toList());
    }

    private UsersDto convertToUsersDto(User user) {
        return UsersDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .otp(user.getOtp())
                .attempt(user.getAttempt())
                .checkOtp(user.getCheckOtp())
                .active(user.isActive())
                .role(user.getRole().name())
                .build();
    }

}
