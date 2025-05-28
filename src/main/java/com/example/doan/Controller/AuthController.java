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
    Optional<users> userByTk = usersRepository.findByTkAndIsDelete(entity.getTk(), false);
    Optional<users> userByEmail = usersRepository.findByEmailAndIsDelete(entity.getEmail(), false);

    // Nếu tài khoản đã tồn tại và chưa bị xóa
    if (userByTk.isPresent() && !Boolean.TRUE.equals(userByTk.get().getIsDelete())) {
        return ResponseEntity.badRequest().body("Tài khoản đã tồn tại và đang hoạt động.");
    }

    // Nếu email đã tồn tại và chưa bị xóa
    if (userByEmail.isPresent() && !Boolean.TRUE.equals(userByEmail.get().getIsDelete())) {
        return ResponseEntity.badRequest().body("Email đã được sử dụng và đang hoạt động.");
    }

    // ✅ Không dùng lại tài khoản cũ đã xóa
    users userToSave = new users();
    userToSave.setTk(entity.getTk());
    userToSave.setMk(entity.getMk());
    userToSave.setFullname(entity.getFullname());
    userToSave.setEmail(entity.getEmail());
    userToSave.setIsDelete(false);

    // Tạo token xác minh
    String token = UUID.randomUUID().toString();
    userToSave.setTokenVerify(token);
    userToSave.setRole("user");
    userToSave.setIsVerify(false);

    usersRepository.save(userToSave);

    // Gửi email xác minh
    Map<String, Object> model = new HashMap<>();
    model.put("name", userToSave.getFullname());
    model.put("email", userToSave.getEmail());
    model.put("token", token);

    MailData mailData = new MailData(
            userToSave.getEmail(),
            "Xác minh tài khoản của bạn",
            "Xin chào " + userToSave.getFullname() + ",\n\n" +
                    "Cảm ơn bạn đã đăng ký tài khoản.\n" +
                    "Email của bạn là: " + userToSave.getEmail() + "\n" +
                    "Vui lòng truy cập liên kết sau để xác minh tài khoản: " +
                    "http://192.168.1.173:8082/api/v1/auth/verify?token=" + token,
            model);

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
