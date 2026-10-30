package com.example.student_management.dao;

import com.example.student_management.entity.Department;
import java.util.List;

public interface DepartmentDAO {
    List<Department> findAll();
    Department findById(Long id);
    Department save(Department department);
    Department update(Department department);
    void deleteById(Long id);
}
