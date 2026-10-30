package com.example.student_management.dao.impl;

import com.example.student_management.dao.DepartmentDAO;
import com.example.student_management.entity.Department;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DepartmentDAOImpl implements DepartmentDAO {

    private final EntityManager entityManager;

    public DepartmentDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Department> findAll() {
        return entityManager
                .createQuery("FROM Department", Department.class)
                .getResultList();
    }

    @Override
    public Department findById(Long id) {
        return entityManager.find(Department.class, id);
    }

    @Override
    public Department save(Department department) {
        entityManager.persist(department);
        return department;
    }

    @Override
    public Department update(Department department) {
        return entityManager.merge(department);
    }

    @Override
    public void deleteById(Long id) {
        Department department = entityManager.find(Department.class, id);

        if (department != null) {
            entityManager.remove(department);
        }
    }

    @Override
    public boolean isReferenced(Long id) {
        Long classCount = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Class c WHERE c.department.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
        Long studentCount = entityManager.createQuery(
                        "SELECT COUNT(s) FROM Student s WHERE s.department.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
        return classCount > 0 || studentCount > 0;
    }
}
