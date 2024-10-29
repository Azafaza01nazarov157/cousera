package org.example.cursera.controller.userRights;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.UserDto;
import org.example.cursera.domain.dtos.auth.request.ChangePasswordRequest;
import org.example.cursera.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "get User By Id")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto =  userService.getUser(id);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("change-password")
    @Operation(summary = "Change Password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Principal user) {
        userService.changePassword(changePasswordRequest, user);
        return new ResponseEntity<>("Password has changed", HttpStatus.OK);
    }

}
