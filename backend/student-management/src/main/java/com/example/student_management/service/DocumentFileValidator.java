package com.example.student_management.service;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class DocumentFileValidator {
    private static final Map<String, Set<String>> ALLOWED_TYPES = Map.of(
            "pdf", Set.of("application/pdf"),
            "doc", Set.of("application/msword"),
            "docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            "ppt", Set.of("application/vnd.ms-powerpoint"),
            "pptx", Set.of("application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            "xls", Set.of("application/vnd.ms-excel"),
            "xlsx", Set.of("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            "csv", Set.of("text/csv", "text/plain", "application/csv"),
            "txt", Set.of("text/plain")
    );

    private final Tika tika = new Tika();

    @Value("${app.documents.max-size-bytes:26214400}")
    private long maxSizeBytes;

    public String validateAndDetect(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File không được để trống");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(413),
                    "Kích thước tối đa là 25 MiB");
        }

        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        Set<String> expectedTypes = ALLOWED_TYPES.get(extension);
        if (expectedTypes == null) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "Định dạng file không được hỗ trợ");
        }

        try (var input = file.getInputStream()) {
            String detectedType = tika.detect(input, originalName);
            if (!expectedTypes.contains(detectedType)) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "Nội dung file không khớp với phần mở rộng");
            }
            return detectedType;
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể đọc nội dung file", exception);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) return "";
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
