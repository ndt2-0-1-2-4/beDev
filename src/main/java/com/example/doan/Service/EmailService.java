package com.example.doan.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import com.example.doan.Model.users;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.utils.MailData;

@Service
public class EmailService {

    @Autowired
    private UsersRepository usersRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMail(MailData mailData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String emailBody = mailData.getTemplateName();
            helper.setFrom(fromEmail);
            helper.setTo(mailData.getEmailToName());
            helper.setSubject(mailData.getEmailSubject());
            helper.setText(emailBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public ResponseEntity<?> verifyEmail(String token) {
        users user = usersRepository.findByTokenVerify(token);
        if (user != null) {
            user.setIsVerify(true);
            user.setTokenVerify("");
            usersRepository.save(user);
            return ResponseEntity.ok("Xác minh thành công");
        } else {
            return ResponseEntity.badRequest().body("Token không tồn tại");
        }
    }
}
