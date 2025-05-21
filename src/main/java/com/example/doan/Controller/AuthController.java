package com.example.doan.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.doan.Model.users;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Service.EmailService;
import com.example.doan.utils.MailData;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            emailService.verifyEmail(token);
            return ResponseEntity.ok("Tài khoản đã được xác minh thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> SignUp(@RequestBody users entity) {
        if (usersRepository.existsByTk(entity.getTk())) {
            return ResponseEntity.badRequest().body("Account is exits");
        }
        if (usersRepository.existsByEmail(entity.getEmail())) {
            return ResponseEntity.badRequest().body("Email is exits");
        }
        String token = UUID.randomUUID().toString();
        entity.setTokenVerify(token);
        entity.setRole("user");
        usersRepository.save(entity);
        Map<String, Object> model = new HashMap<>();
        model.put("name", entity.getFullname()); // Truyền name vào template
        model.put("email", entity.getEmail()); // Truyền email vào template (nếu cần)
        model.put("token", token);
        // Tạo đối tượng MailData
        MailData mailData = new MailData(
                entity.getEmail(), // emailToName: Địa chỉ email người nhận
                "Xác minh tài khoản của bạn", // emailSubject: Chủ đề email
                "verify-email", // templateName: Tên template Thymeleaf (verify-email.html)
                model);

        // Gửi email
        emailService.sendMail(mailData);
        return ResponseEntity.ok("User registered successfully!");
    }

}
