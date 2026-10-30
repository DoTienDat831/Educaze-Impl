package com.example.student_management.service;

import com.example.student_management.dao.DepartmentDAO;
import com.example.student_management.entity.Department;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDAO departmentDAO;

    public DepartmentServiceImpl(DepartmentDAO departmentDAO) {
        this.departmentDAO = departmentDAO;
    }

    @Override
    @Transactional
    public List<Department> findAll() {
        return departmentDAO.findAll();
    }

    @Override
    @Transactional
    public Department findById(Long id) {
        return departmentDAO.findById(id);
    }

    @Override
    @Transactional
    public Department save(Department department) {

        return departmentDAO.save(department);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        departmentDAO.deleteById(id);
    }
}