package com.example.student_management.dto;

import com.example.student_management.entity.AppRole;

public record AuthResponse(String accessToken, String tokenType, long expiresIn, String username, AppRole role) {}
