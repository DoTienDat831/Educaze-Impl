package com.example.student_management.dao.impl;

import com.example.student_management.dao.DocumentDAO;
import com.example.student_management.entity.Document;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Repository
@Transactional
public class DocumentDAOImpl implements DocumentDAO {
    private final EntityManager entityManager;

    public DocumentDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Document save(Document document) {
        entityManager.persist(document);
        entityManager.flush();
        return document;
    }

    @Override
    public List<Document> findAllActive() {
        return entityManager.createQuery(
                        "SELECT d FROM Document d WHERE d.deleted = false ORDER BY d.id DESC",
                        Document.class)
                .getResultList();
    }

    @Override
    public Document findByPk(String id) {
        try {
            return entityManager.find(Document.class, Long.valueOf(id));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    public Document findByObjectKey(String objectKey) {
        return entityManager.createQuery(
                        "SELECT d FROM Document d WHERE d.objectKey = :objectKey AND d.deleted = false",
                        Document.class)
                .setParameter("objectKey", objectKey)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Document update(Document document) {
        return entityManager.merge(document);
    }

    @Override
    public List<Document> findDeletedBefore(Instant cutoff) {
        return entityManager.createQuery(
                        "SELECT d FROM Document d WHERE d.deleted = true " +
                                "AND (d.deletedAt IS NULL OR d.deletedAt <= :cutoff)",
                        Document.class)
                .setParameter("cutoff", cutoff)
                .getResultList();
    }

    @Override
    public void deletePermanently(Long id) {
        entityManager.createQuery("DELETE FROM Document d WHERE d.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}
