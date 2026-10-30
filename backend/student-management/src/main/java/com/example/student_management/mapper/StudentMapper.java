package com.example.student_management.mapper;

import com.example.student_management.dto.ClassResponse;
import com.example.student_management.dto.StudentRequest;
import com.example.student_management.dto.StudentResponse;
import com.example.student_management.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {
    private final DepartmentMapper departmentMapper;
    private final ClassMapper classMapper;

    public StudentMapper(DepartmentMapper departmentMapper, ClassMapper classMapper) {
        this.departmentMapper = departmentMapper;
        this.classMapper = classMapper;
    }

    public void updateEntity(Student student, StudentRequest request) {
        student.setName(request.name().trim());
        student.setDateOfBirth(request.dateOfBirth());
        student.setGender(request.gender() == null ? null : request.gender().trim());
        student.setEnrollmentTime(request.enrollmentTime());
        student.setEmail(request.email() == null ? null : request.email().trim());
    }

    public StudentResponse toResponse(Student student) {
        ClassResponse studentClass = student.getStudentClass() == null
                ? null : classMapper.toResponse(student.getStudentClass());
        return new StudentResponse(student.getId(), student.getStudentCode(), student.getName(),
                student.getDateOfBirth(), student.getGender(), student.getEnrollmentTime(), student.getEmail(),
                departmentMapper.toResponse(student.getDepartment()), studentClass);
    }
}
