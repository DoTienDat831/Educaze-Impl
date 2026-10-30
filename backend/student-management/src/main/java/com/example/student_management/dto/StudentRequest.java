package com.example.student_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StudentRequest(
        @NotBlank @Size(max = 150) String name,
        @Past LocalDate dateOfBirth,
        @Size(max = 10) String gender,
        LocalDate enrollmentTime,
        @Email @Size(max = 254) String email,
        @NotNull Long departmentId,
        Long classId
) {}
