package com.example.student_management.service;

import com.example.student_management.dao.DocumentDAO;
import com.example.student_management.dto.DocumentResponse;
import com.example.student_management.entity.Document;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    private final MinioClient minioClient;
    private final DocumentDAO documentDAO;
    private final DocumentFileValidator fileValidator;

    @Value("${app.minio.bucket}")
    private String bucket;

    public DocumentService(MinioClient minioClient, DocumentDAO documentDAO,
                           DocumentFileValidator fileValidator) {
        this.minioClient = minioClient;
        this.documentDAO = documentDAO;
        this.fileValidator = fileValidator;
    }

    @Transactional
    public DocumentResponse upload(MultipartFile file) {
        String contentType = fileValidator.validateAndDetect(file);
        String originalName = safeOriginalName(file.getOriginalFilename());
        String extension = getExtension(originalName);
        String objectKey = UUID.randomUUID() + "." + extension;
        try (InputStream input = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(input, file.getSize(), (long) -1)
                    .contentType(contentType)
                    .build());

            Document document = new Document();
            document.setName(originalName);
            document.setObjectKey(objectKey);
            document.setContentType(contentType);
            document.setSize(file.getSize());
            return DocumentResponse.from(documentDAO.save(document));
        } catch (Exception exception) {
            // If metadata persistence fails after the object upload, compensate by removing it.
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(bucket).object(objectKey).build());
            } catch (Exception cleanupException) {
                logger.error("Could not clean up uploaded object {}", objectKey, cleanupException);
            }
            logger.error("Document upload failed", exception);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Không thể upload tài liệu", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> findAll() {
        return documentDAO.findAllActive().stream().map(DocumentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ResponseEntity<StreamingResponseBody> download(Long id) {
        Document document = fetchAndValidateDocumentById(id);
        StreamingResponseBody body = output -> {
            try (InputStream input = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(document.getObjectKey())
                    .build())) {
                input.transferTo(output);
            } catch (Exception exception) {
                logger.error("Document download failed for id {}", id, exception);
                throw new java.io.IOException("Không thể tải tài liệu từ MinIO", exception);
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .contentLength(document.getSize())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename(document.getName(), StandardCharsets.UTF_8)
                                .build().toString())
                .body(body);
    }

    @Transactional
    public void delete(Long id) {
        Document document = fetchAndValidateDocumentById(id);
        document.setDeleted(true);
        document.setDeletedAt(Instant.now());
        documentDAO.update(document);
    }

    public Document fetchAndValidateDocumentById(Long id) {
        Document document = id == null ? null : documentDAO.findByPk(id.toString());
        if (document == null || document.isDeleted()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy tài liệu");
        }
        return document;
    }

    private String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(java.util.Locale.ROOT);
    }

    private String safeOriginalName(String filename) {
        if (filename == null || filename.isBlank()) return "document";
        // Do not retain any client-supplied path components.
        String normalized = filename.replace('\\', '/');
        return normalized.substring(normalized.lastIndexOf('/') + 1);
    }
}
