package com.example.student_management.dto;

import com.example.student_management.entity.Document;

public record DocumentResponse(
        Long id,
        String name,
        String contentType,
        long size
) {
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getName(),
                document.getContentType(),
                document.getSize());
    }
}
