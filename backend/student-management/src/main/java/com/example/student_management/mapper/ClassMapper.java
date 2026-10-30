package com.example.student_management.mapper;

import com.example.student_management.dto.ClassRequest;
import com.example.student_management.dto.ClassResponse;
import com.example.student_management.entity.Class;
import org.springframework.stereotype.Component;

@Component
public class ClassMapper {
    private final DepartmentMapper departmentMapper;

    public ClassMapper(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    public void updateEntity(Class studentClass, ClassRequest request) {
        studentClass.setName(request.name().trim());
    }

    public ClassResponse toResponse(Class studentClass) {
        return new ClassResponse(studentClass.getId(), studentClass.getName(),
                departmentMapper.toResponse(studentClass.getDepartment()));
    }
}
