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
        if (department.getId() == null) {
            entityManager.persist(department);
        } else {
            entityManager.merge(department);
        }

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
            // Class.department is the owning side of the relationship and
            // Class.department_id references this row. Keep classes and students
            // intact by removing that association first.
            entityManager.createQuery(
                    "UPDATE Class m SET m.department = null WHERE m.department.id = :departmentId")
                    .setParameter("departmentId", id)
                    .executeUpdate();

            entityManager.remove(department);
        }
    }
}
