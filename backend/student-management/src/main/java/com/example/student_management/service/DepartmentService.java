package com.example.student_management.service;

import com.example.student_management.entity.Department;

import java.util.List;

public interface DepartmentService {

    List<Department> findAll();

    Department findById(Long id);

    Department save(Department department);

    void deleteById(Long id);
}