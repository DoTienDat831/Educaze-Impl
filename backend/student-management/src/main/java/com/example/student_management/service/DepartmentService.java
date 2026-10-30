package com.example.student_management.service;

import com.example.student_management.dto.DepartmentRequest;
import com.example.student_management.dto.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    List<DepartmentResponse> findAll();

    DepartmentResponse findById(Long id);

    DepartmentResponse create(DepartmentRequest request);

    DepartmentResponse update(Long id, DepartmentRequest request);

    void deleteById(Long id);
}
