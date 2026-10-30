package com.example.student_management.mapper;

import com.example.student_management.dto.DocumentResponse;
import com.example.student_management.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class DocumentMapper {
    public DocumentResponse toResponse(Document document) {
        return new DocumentResponse(document.getId(), document.getName(), document.getContentType(), document.getSize());
    }
}
