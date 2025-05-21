package com.example.doan.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.doan.Model.users;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.utils.MailData;

@Service
public class PasswordService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UsersRepository usersRepository;

    @Transactional
    public ResponseEntity<?> handleForgetPassword(users request) {
        users u = usersRepository.findByEmailAndIsDelete(request.getEmail(), false);
        if (u == null) {
            return ResponseEntity.badRequest().body("Tài khoản không tồn tại");
        }
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);
        String token = UUID.randomUUID().toString();
        u.setExpiryRecovery(expirationTime);
        u.setTokenVerify(token);
        usersRepository.save(u);
        Map<String, Object> model = new HashMap<>();
        model.put("name", u.getFullname());
        model.put("email", request.getEmail());
        String resetlink = "http://localhost:8082/api/v1/auth/reset-password?token=" + token;
        MailData mailData = new MailData(
                u.getEmail(),
                "[SBCB] Kết quả khôi phục mật khẩu!",
                "<p>Xin chào,</p>"
                        + "<p>Vui lòng nhấp vào liên kết dưới đây để đặt lại mật khẩu của bạn:</p>"
                        + "<p><a href=\"" + resetlink + "\">Đặt lại mật khẩu</a></p>"
                        + "<p>Liên kết này sẽ hết hạn sau 24 giờ.</p>",
                model);
        emailService.sendMail(mailData);
        return ResponseEntity.ok("Yêu cầu otp thành công");
    }

    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
    // public ResponseEntity<?> verifyOtpPass(users entity){

    // }
    public ResponseEntity<?> resetPassword(String token, String newPassword) {
        users u = usersRepository.findByTokenVerify(token);
        if (u != null) {
            u.setMk(newPassword);
            u.setTokenVerify("");
            usersRepository.save(u);
            return ResponseEntity.ok("Successfully");
        } else {
            return ResponseEntity.badRequest().body("Request expired");
        }
    }
}
