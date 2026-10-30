package com.example.student_management.service;

import com.example.student_management.dao.ClassDAO;
import com.example.student_management.dao.DepartmentDAO;
import com.example.student_management.dao.StudentDAO;
import com.example.student_management.dto.StudentRequest;
import com.example.student_management.dto.StudentResponse;
import com.example.student_management.entity.Class;
import com.example.student_management.entity.Department;
import com.example.student_management.entity.Student;
import com.example.student_management.mapper.StudentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudentService {
    private final StudentDAO studentDAO;
    private final DepartmentDAO departmentDAO;
    private final ClassDAO classDAO;
    private final StudentMapper studentMapper;

    public StudentService(StudentDAO studentDAO, DepartmentDAO departmentDAO,
            ClassDAO classDAO, StudentMapper studentMapper) {
        this.studentDAO = studentDAO;
        this.departmentDAO = departmentDAO;
        this.classDAO = classDAO;
        this.studentMapper = studentMapper;
    }

    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        Student student = new Student();
        studentMapper.updateEntity(student, request);
        Department department = resolveDepartment(request.departmentId());
        student.setDepartment(department);
        student.setStudentClass(resolveClass(request.classId(), department));

        studentDAO.save(student);
        student.setStudentCode(String.format("VJU%04d", student.getId()));
        return studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        Student student = studentDAO.findById(id);
        return student == null ? null : studentMapper.toResponse(student);
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        return studentDAO.findAll().stream().map(studentMapper::toResponse).toList();
    }

    @Transactional
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student existing = studentDAO.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên");
        }
        studentMapper.updateEntity(existing, request);
        Department department = resolveDepartment(request.departmentId());
        existing.setDepartment(department);
        // A null classId in PUT explicitly unassigns the student from their class.
        existing.setStudentClass(resolveClass(request.classId(), department));
        studentDAO.update(existing);
        return studentMapper.toResponse(existing);
    }

    @Transactional
    public void deleteStudent(Long id) {
        if (studentDAO.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên");
        }
        studentDAO.delete(id);
    }

    private Department resolveDepartment(Long departmentId) {
        Department department = departmentId == null ? null : departmentDAO.findById(departmentId);
        if (department == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "departmentId không hợp lệ");
        }
        return department;
    }

    private Class resolveClass(Long classId, Department department) {
        if (classId == null) return null;
        Class studentClass = classDAO.findById(classId);
        if (studentClass == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "classId không hợp lệ");
        }
        if (studentClass.getDepartment() == null
                || !studentClass.getDepartment().getId().equals(department.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lớp được chọn không thuộc khoa đã chọn");
        }
        return studentClass;
    }
}
