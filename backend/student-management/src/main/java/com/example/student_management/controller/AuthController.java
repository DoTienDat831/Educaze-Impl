package com.example.student_management.controller;

import com.example.student_management.dto.AuthResponse;
import com.example.student_management.dto.CreateUserRequest;
import com.example.student_management.dto.LoginRequest;
import com.example.student_management.dto.UserResponse;
import com.example.student_management.dto.UpdateRoleRequest;
import com.example.student_management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password());
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return authService.createUser(request);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUsers() {
        return authService.listUsers();
    }

    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request) {
        return authService.updateRole(id, request.role());
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void disableUser(@PathVariable Long id, Authentication authentication) {
        authService.disableUser(id, authentication.getName());
    }
}
