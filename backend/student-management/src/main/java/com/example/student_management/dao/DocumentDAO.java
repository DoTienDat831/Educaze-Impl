package com.example.student_management.dao;

import com.example.student_management.entity.Document;
import java.time.Instant;
import java.util.List;

public interface DocumentDAO {
    Document save(Document document);
    List<Document> findAllActive();
    Document findByPk(String id);
    Document findByObjectKey(String objectKey);
    Document update(Document document);
    List<Document> findDeletedBefore(Instant cutoff);
    void deletePermanently(Long id);
}
