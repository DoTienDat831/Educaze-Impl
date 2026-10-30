package com.example.student_management.dao.impl;

import com.example.student_management.dao.ClassDAO;
import com.example.student_management.entity.Class;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public class ClassDAOImpl implements ClassDAO {
    @PersistenceContext private EntityManager entityManager;
    @Override public Class save(Class studentClass) { entityManager.persist(studentClass); return studentClass; }
    @Override public Class findById(Long id) { return entityManager.find(Class.class, id); }
    @Override public List<Class> findAll() { return entityManager.createQuery("SELECT c FROM Class c", Class.class).getResultList(); }
    @Override public void update(Class studentClass) { entityManager.merge(studentClass); }
    @Override public void delete(Long id) { Class classEntity = entityManager.find(Class.class, id); if (classEntity != null) entityManager.remove(classEntity); }
    @Override public boolean hasStudents(Long id) {
        return entityManager.createQuery("SELECT COUNT(s) FROM Student s WHERE s.studentClass.id = :id", Long.class)
                .setParameter("id", id).getSingleResult() > 0;
    }
}
