package com.example.student_management.dto;

import com.example.student_management.entity.AppRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 100) String username,
        @NotBlank @Size(min = 12, max = 72) String password,
        @NotNull AppRole role
) {}
