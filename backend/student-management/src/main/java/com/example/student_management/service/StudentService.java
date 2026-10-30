package com.example.student_management.service;

import com.example.student_management.dao.StudentDAO;
import com.example.student_management.dao.DepartmentDAO;
import com.example.student_management.entity.Department;
import com.example.student_management.entity.Student;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StudentService {

    private final StudentDAO studentDAO;
    private final DepartmentDAO departmentDAO;

    public StudentService(StudentDAO studentDAO, DepartmentDAO departmentDAO) {
        this.studentDAO = studentDAO;
        this.departmentDAO = departmentDAO;
    }

    @Transactional
    public Student createStudent(Student student) {
        student.setDepartment(resolveDepartment(student.getDepartment()));
        student.setStudentClass(null);

        // Lưu Student trước để MySQL sinh ID
        studentDAO.save(student);

        // Tạo studentCode từ ID
        String studentCode =
                String.format("VJU%04d", student.getId());

        student.setStudentCode(studentCode);

        return student;
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentDAO.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    @Transactional
    public Student updateStudent(Long id, Student student) {

        Student existingStudent = studentDAO.findById(id);

        if (existingStudent == null) {
            return null;
        }

        existingStudent.setName(student.getName());
        existingStudent.setDateOfBirth(student.getDateOfBirth());
        existingStudent.setEnrollmentTime(student.getEnrollmentTime());
        existingStudent.setEmail(student.getEmail());
        existingStudent.setDepartment(resolveDepartment(student.getDepartment()));
        existingStudent.setStudentClass(null);

        // Không cho phép update studentCode
        // studentCode được sinh một lần khi tạo Student

        studentDAO.update(existingStudent);

        return existingStudent;
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentDAO.delete(id);
    }

    private Department resolveDepartment(Department requestedDepartment) {
        Department department = requestedDepartment == null || requestedDepartment.getId() == null
                ? null
                : departmentDAO.findById(requestedDepartment.getId());
        if (department == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department does not exist");
        }
        return department;
    }
}
