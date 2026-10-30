package com.example.student_management.service;

import com.example.student_management.dao.DepartmentDAO;
import com.example.student_management.dto.DepartmentRequest;
import com.example.student_management.dto.DepartmentResponse;
import com.example.student_management.entity.Department;
import com.example.student_management.mapper.DepartmentMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentDAO departmentDAO;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentDAO departmentDAO, DepartmentMapper departmentMapper) {
        this.departmentDAO = departmentDAO;
        this.departmentMapper = departmentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return departmentDAO.findAll().stream().map(departmentMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse findById(Long id) {
        Department department = departmentDAO.findById(id);
        return department == null ? null : departmentMapper.toResponse(department);
    }

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        Department department = departmentMapper.toEntity(request);
        return departmentMapper.toResponse(departmentDAO.save(department));
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department existing = departmentDAO.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khoa");
        }
        departmentMapper.updateEntity(existing, request);
        return departmentMapper.toResponse(departmentDAO.update(existing));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Department department = departmentDAO.findById(id);
        if (department == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khoa");
        }
        if (departmentDAO.isReferenced(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Không thể xóa khoa đang được lớp hoặc sinh viên sử dụng");
        }
        departmentDAO.deleteById(id);
    }
}
