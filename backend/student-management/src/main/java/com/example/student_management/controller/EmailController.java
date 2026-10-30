package com.example.student_management.controller;

import com.example.student_management.dao.StudentDAO;
import com.example.student_management.dto.StudentEmailRequest;
import com.example.student_management.entity.Student;
import com.example.student_management.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final StudentDAO studentDAO;
    private final EmailService emailService;

    public EmailController(StudentDAO studentDAO, EmailService emailService) {
        this.studentDAO = studentDAO;
        this.emailService = emailService;
    }

    @PostMapping("/students/{studentId}")
    public ResponseEntity<Void> sendToStudent(
            @PathVariable Long studentId,
            @Valid @RequestBody StudentEmailRequest request) {
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sinh viên");
        }
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sinh viên chưa có địa chỉ email");
        }

        try {
            emailService.sendStudentNotification(
                    student.getEmail(), student.getName(), request.subject(), request.message());
        } catch (EmailService.EmailDeliveryException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Gửi email thất bại", ex);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), ex);
        }
        return ResponseEntity.noContent().build();
    }

}
