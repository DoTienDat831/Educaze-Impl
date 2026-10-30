package com.example.student_management.dto;

public record DocumentResponse(
        Long id,
        String name,
        String contentType,
        long size
) {}
