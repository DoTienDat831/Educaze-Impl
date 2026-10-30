package com.example.student_management.dao;

import com.example.student_management.entity.Class;
import java.util.List;

public interface ClassDAO {
    Class save(Class studentClass);
    Class findById(Long id);
    List<Class> findAll();
    void update(Class studentClass);
    void delete(Long id);
}
