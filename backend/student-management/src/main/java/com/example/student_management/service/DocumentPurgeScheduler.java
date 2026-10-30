package com.example.student_management.service;

import com.example.student_management.dao.DocumentDAO;
import com.example.student_management.entity.Document;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class DocumentPurgeScheduler {
    private static final Logger logger = LoggerFactory.getLogger(DocumentPurgeScheduler.class);

    private final MinioClient minioClient;
    private final DocumentDAO documentDAO;
    private final String bucket;

    @Value("${app.documents.deleted-retention-days:30}")
    private long retentionDays;

    public DocumentPurgeScheduler(MinioClient minioClient, DocumentDAO documentDAO,
                                  @Value("${app.minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.documentDAO = documentDAO;
        this.bucket = bucket;
    }

    @Scheduled(fixedDelayString = "${app.documents.purge-delay-ms:3600000}")
    public void purgeExpiredDocuments() {
        Instant cutoff = Instant.now().minus(Duration.ofDays(retentionDays));
        for (Document document : documentDAO.findDeletedBefore(cutoff)) {
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(document.getObjectKey())
                        .build());
                documentDAO.deletePermanently(document.getId());
            } catch (Exception exception) {
                logger.error("Could not purge expired document id {}", document.getId(), exception);
            }
        }
    }
}
