package org.example.cursera.controller.userRights;

import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.UsersDto;
import org.example.cursera.domain.enums.Role;
import org.example.cursera.service.user.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get all users", description = "Retrieves a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/users")
    public ResponseEntity<List<UsersDto>> getAllUsers(@RequestParam Long adminId) {
        List<UsersDto> users = adminService.findAllUsers(adminId);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Create a new moderator", description = "Creates a moderator account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moderator created successfully"),
            @ApiResponse(responseCode = "404", description = "Admin not found"),
            @ApiResponse(responseCode = "400", description = "Invalid moderator email")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/moderators")
    public ResponseEntity<String> createModerator(@RequestParam Long adminId, @RequestParam String moderatorEmail) {
        adminService.createModerator(adminId, moderatorEmail);
        return ResponseEntity.ok("Moderator created successfully");
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Admin or user not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@RequestParam Long adminId, @PathVariable Long userId) {
        adminService.deleteUser(adminId, userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @Operation(summary = "Update user role", description = "Updates the role of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role updated successfully"),
            @ApiResponse(responseCode = "404", description = "Admin or user not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> updateUserRole(
            @RequestParam Long adminId,
            @PathVariable Long userId,
            @RequestParam Role newRole) {
        adminService.updateUserRole(adminId, userId, newRole);
        return ResponseEntity.ok("User role updated successfully");
    }

    @Operation(summary = "Get all moderators", description = "Retrieves a list of all moderators.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Moderators retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/moderators")
    public ResponseEntity<List<UsersDto>> getAllModerators(@RequestParam Long adminId) {
        List<UsersDto> moderators = adminService.findAllModerators(adminId);
        return ResponseEntity.ok(moderators);
    }

    @Operation(summary = "Get all active users", description = "Retrieves a list of all active users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Admin not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/users/active")
    public ResponseEntity<List<UsersDto>> getAllActiveUsers(@RequestParam Long adminId) {
        List<UsersDto> activeUsers = adminService.findAllActiveUsers(adminId);
        return ResponseEntity.ok(activeUsers);
    }
}
