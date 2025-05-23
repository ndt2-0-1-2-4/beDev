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
import com.example.doan.Service.PasswordService;
import com.example.doan.utils.MailData;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UsersRepository usersRepository;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        try {
            return emailService.verifyEmail(token);
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
        model.put("name", entity.getFullname());
        model.put("email", entity.getEmail());
        model.put("token", token);
        // Tạo đối tượng MailData
        MailData mailData = new MailData(
                entity.getEmail(),
                "Xác minh tài khoản của bạn",
                "Xin chào " + entity.getFullname() + ",\n\n" +
                        "Cảm ơn bạn đã đăng ký tài khoản.\n" +
                        "Email của bạn là: " + entity.getEmail() + "\n" +
                        "Vui lòng truy cập liên kết sau để xác minh tài khoản: " +
                        "http://localhost:8082/api/v1/auth/verify?token=" + token,
                model);

        // Gửi email
        emailService.sendMail(mailData);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);

    }

    @PostMapping("/forget-pass")
    public ResponseEntity forgetPass(@RequestBody users entity) {
        return passwordService.handleForgetPassword(entity);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody users entity) {
        try {
            String newPassword = entity.getMk();
            return passwordService.resetPassword(token, newPassword);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify-otp")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

}
