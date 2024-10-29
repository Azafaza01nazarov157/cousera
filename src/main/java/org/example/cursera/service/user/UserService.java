package org.example.cursera.service.user;


import org.example.cursera.domain.dtos.UserDto;
import org.example.cursera.domain.dtos.auth.request.ChangePasswordRequest;
import org.example.cursera.domain.entity.User;

import java.security.Principal;

public interface UserService {
    void changePassword(ChangePasswordRequest request, Principal connectedUser);

    UserDto getUser(Long userId);

    User findById(Long userId);

    User findByEmail(String email);
}
