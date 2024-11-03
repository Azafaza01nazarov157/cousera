package org.example.cursera.service.user;

import org.example.cursera.domain.dtos.UsersDto;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.enums.Role;

import java.util.List;

public interface AdminService {

    List<UsersDto> findAllUsers(Long userId);

    List<UsersDto> findAllMODERATOR(Long userId);

    void createModerator(Long adminId, String moderatorEmail);

    void deleteUser(Long adminId, Long userId);

    void updateUserRole(Long adminId, Long userId, String newRole);

    List<UsersDto> findAllModerators(Long adminId);

    List<UsersDto> findAllActiveUsers(Long adminId);
}
