package com.example.student_management.dto;

import com.example.student_management.entity.AppRole;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull AppRole role) {}
