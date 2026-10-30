package com.example.student_management.service;

import com.example.student_management.dao.ClassDAO;
import com.example.student_management.dao.DepartmentDAO;
import com.example.student_management.dto.ClassRequest;
import com.example.student_management.dto.ClassResponse;
import com.example.student_management.entity.Class;
import com.example.student_management.entity.Department;
import com.example.student_management.mapper.ClassMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClassService {
    private final ClassDAO classDAO;
    private final DepartmentDAO departmentDAO;
    private final ClassMapper classMapper;

    public ClassService(ClassDAO classDAO, DepartmentDAO departmentDAO, ClassMapper classMapper) {
        this.classDAO = classDAO;
        this.departmentDAO = departmentDAO;
        this.classMapper = classMapper;
    }

    @Transactional
    public ClassResponse createClass(ClassRequest request) {
        Class studentClass = new Class();
        classMapper.updateEntity(studentClass, request);
        studentClass.setDepartment(resolveDepartment(request.departmentId()));
        classDAO.save(studentClass);
        return classMapper.toResponse(studentClass);
    }

    @Transactional(readOnly = true)
    public List<ClassResponse> getAllClasses() {
        return classDAO.findAll().stream().map(classMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClassResponse getClassById(Long id) {
        Class studentClass = classDAO.findById(id);
        return studentClass == null ? null : classMapper.toResponse(studentClass);
    }

    @Transactional
    public ClassResponse updateClass(Long id, ClassRequest request) {
        Class existing = classDAO.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp");
        }
        classMapper.updateEntity(existing, request);
        existing.setDepartment(resolveDepartment(request.departmentId()));
        classDAO.update(existing);
        return classMapper.toResponse(existing);
    }

    @Transactional
    public void deleteClass(Long id) {
        if (classDAO.findById(id) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy lớp");
        }
        if (classDAO.hasStudents(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể xóa lớp đang có sinh viên");
        }
        classDAO.delete(id);
    }

    private Department resolveDepartment(Long departmentId) {
        Department department = departmentDAO.findById(departmentId);
        if (department == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "departmentId không hợp lệ");
        }
        return department;
    }
}
