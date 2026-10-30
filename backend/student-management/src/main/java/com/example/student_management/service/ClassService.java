package com.example.student_management.service;

import com.example.student_management.dao.ClassDAO;
import com.example.student_management.entity.Class;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassService {
    private final ClassDAO classDAO;
    public ClassService(ClassDAO classDAO) { this.classDAO = classDAO; }
    public Class createClass(Class studentClass) { return classDAO.save(studentClass); }
    public List<Class> getAllClasses() { return classDAO.findAll(); }
    public Class getClassById(Long id) { return classDAO.findById(id); }
    public Class updateClass(Long id, Class studentClass) {
        Class existingClass = classDAO.findById(id);
        if (existingClass == null) return null;
        existingClass.setName(studentClass.getName());
        existingClass.setDepartment(studentClass.getDepartment());
        classDAO.update(existingClass);
        return existingClass;
    }
    public void deleteClass(Long id) { classDAO.delete(id); }
}
