package com.example.doan.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
            String emailBody = "Xin chào " + mailData.getModel().get("name") + ",\n\n" +
                    "Cảm ơn bạn đã đăng ký tài khoản.\n" +
                    "Email của bạn là: " + mailData.getModel().get("email") + "\n" +
                    "Vui lòng truy cập liên kết sau để xác minh tài khoản: " +
                    "http://localhost:8082/api/v1/auth/verify?token=" + mailData.getModel().get("token");

            helper.setFrom(fromEmail);
            helper.setTo(mailData.getEmailToName());
            helper.setSubject(mailData.getEmailSubject());
            helper.setText(emailBody, true);
            users u = new users();
            u.setEmail(mailData.getEmailToName());
            u.setTokenVerify((String) mailData.getModel().get("token"));
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String renderTemplate(String templateName, Map<String, Object> model) {
        Context context = new Context();
        model.forEach(context::setVariable);
        return templateEngine.process(templateName, context);
    }

    public void verifyEmail(String token) {
        Optional<users> user = usersRepository.findByTokenVerify(token);
        if (user != null) {
            user.get().setIsVerify(true);
            usersRepository.save(user.get());
        } else {
            throw new RuntimeException("Invalid verification token");
        }
    }
}
