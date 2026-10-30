package com.example.student_management.service;

import com.example.student_management.config.MailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final MailProperties mailProperties;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine,
                        MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.mailProperties = mailProperties;
    }

    public void sendStudentNotification(String to, String studentName,
                                        String subject, String body) {
        if (fromEmail.isBlank()) {
            throw new IllegalStateException("Chưa cấu hình địa chỉ gửi email (MAIL_USERNAME)");
        }

        Context context = new Context();
        context.setVariable("schoolName", mailProperties.fromName());
        context.setVariable("studentName", studentName);
        context.setVariable("subject", subject);
        context.setVariable("body", body);
        String html = templateEngine.process("email/student-notification", context);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(fromEmail, mailProperties.fromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, html);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new IllegalStateException("Không thể tạo email", ex);
        } catch (MailException ex) {
            throw new EmailDeliveryException("Không gửi được email đến " + to, ex);
        }
    }

    public static class EmailDeliveryException extends RuntimeException {
        public EmailDeliveryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
