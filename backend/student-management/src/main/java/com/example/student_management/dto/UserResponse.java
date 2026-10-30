package com.example.student_management.dto;

import com.example.student_management.entity.AppRole;

public record UserResponse(Long id, String username, AppRole role, boolean enabled) {}
