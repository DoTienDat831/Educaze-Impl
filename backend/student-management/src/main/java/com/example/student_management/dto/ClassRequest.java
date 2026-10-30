package com.example.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClassRequest(
        @NotBlank @Size(max = 150) String name,
        @NotNull Long departmentId
) {}
