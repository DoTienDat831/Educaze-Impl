package com.example.student_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentEmailRequest(
        @NotBlank @Size(max = 200) String subject,
        @NotBlank @Size(max = 10000) String message
) {}
