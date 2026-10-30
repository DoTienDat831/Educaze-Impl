package com.example.student_management.dto;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String studentCode,
        String name,
        LocalDate dateOfBirth,
        String gender,
        LocalDate enrollmentTime,
        String email,
        DepartmentResponse department,
        ClassResponse studentClass
) {}
