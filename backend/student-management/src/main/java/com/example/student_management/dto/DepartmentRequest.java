package com.example.student_management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DepartmentRequest(
        @NotBlank @Size(max = 50) String departmentCode,
        @NotBlank @Size(max = 150) String departmentName,
        @NotNull @DecimalMin("0.00") @Digits(integer = 12, fraction = 2) BigDecimal tuitionFee
) {}
