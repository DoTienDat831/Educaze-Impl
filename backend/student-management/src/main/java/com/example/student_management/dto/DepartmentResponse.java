package com.example.student_management.dto;

import java.math.BigDecimal;

public record DepartmentResponse(Long id, String departmentCode, String departmentName, BigDecimal tuitionFee) {}
