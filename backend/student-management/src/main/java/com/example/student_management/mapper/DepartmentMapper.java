package com.example.student_management.mapper;

import com.example.student_management.dto.DepartmentRequest;
import com.example.student_management.dto.DepartmentResponse;
import com.example.student_management.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {
    public Department toEntity(DepartmentRequest request) {
        Department department = new Department();
        updateEntity(department, request);
        return department;
    }

    public void updateEntity(Department department, DepartmentRequest request) {
        department.setDepartmentCode(request.departmentCode().trim());
        department.setDepartmentName(request.departmentName().trim());
        department.setTuitionFee(request.tuitionFee());
    }

    public DepartmentResponse toResponse(Department department) {
        if (department == null) return null;
        return new DepartmentResponse(department.getId(), department.getDepartmentCode(),
                department.getDepartmentName(), department.getTuitionFee());
    }
}
