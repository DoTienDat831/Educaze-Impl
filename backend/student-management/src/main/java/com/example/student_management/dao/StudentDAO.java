package com.example.student_management.dao;

import com.example.student_management.entity.Student;
import java.util.List;

public interface StudentDAO {
    Student save(Student student);
    Student findById(Long id);
    List<Student> findAll();
    void update(Student student);
    void delete(Long id);

}
