package com.example.doan.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.doan.Model.users;
import com.example.doan.Repository.HisBalanceRepo;
import com.example.doan.Repository.MessageRepo;
import com.example.doan.Model.JwtUtil;
import com.example.doan.Model.friend;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.atmRepository;
import com.example.doan.Repository.betHisfbxsRepo;
import com.example.doan.Repository.friendRepository;
import com.example.doan.Repository.sessionPlayerRepo;
import com.example.doan.Service.EmailService;
import com.example.doan.Service.UserService;
import com.example.doan.utils.MailData;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("user")
public class usersController {
    HttpServletRequest request;

    @Autowired
    private MessageRepo MessageRepo;
    @Autowired
    private betHisfbxsRepo betHisfbxsRepo;
    @Autowired
    private friendRepository friendRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private HisBalanceRepo hisBalanceRepo;
    @Autowired
    private sessionPlayerRepo sessionPlayerRepo;

    @Autowired
    private atmRepository atmRepository;
    private static String fullname = "";
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<users> getUsersById() {
        return usersRepository.findById(1).map(users -> ResponseEntity.ok(users))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login") // Đăng nhập
public ResponseEntity<?> login(@Valid @RequestBody users loginRequest, HttpServletResponse response) {
    Optional<users> user = usersRepository.findByTkAndIsDelete(loginRequest.getTk() , false);

    if (user.isPresent()) {
        users u = user.get();

        // Kiểm tra nếu tài khoản đã bị xóa hoặc vô hiệu hóa
        if (Boolean.TRUE.equals(u.getIsDelete())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản đã bị xóa hoặc vô hiệu hóa.");
        }

        // Kiểm tra nếu is_token = true => chặn đăng nhập
        if (Boolean.TRUE.equals(u.getTokenVerify())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Tài khoản này đã có token và không được phép đăng nhập lại.");
        }

        // Cho phép đăng nhập nếu đã được xác minh (is_verified = true) và đúng mật khẩu
        if (Boolean.TRUE.equals(u.getIsVerify()) && u.getMk().equals(loginRequest.getMk())) {
            Cookie cookieId = new Cookie("id", String.valueOf(u.getId()));
            cookieId.setMaxAge(60 * 60 * 24 * 7);
            fullname = u.getFullname();
            response.addCookie(cookieId);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("id", u.getId());
            responseBody.put("fullname", u.getFullname());

            // Token
            String token = jwtUtil.generateToken(u.getTk(), u.getRole());
            responseBody.put("token", token);

            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản chưa xác minh hoặc sai mật khẩu.");
        }
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Sai tài khoản hoặc mật khẩu");
}


    @PostMapping("/regis") // Đăng ký
    public ResponseEntity<?> regis(@RequestBody users entity) {
        Optional<users> user = usersRepository.findByTk(entity.getTk());
        Map<String, Object> response = new HashMap<>();
        if (user.isPresent()) {
            response.put("status", "error");
            response.put("message", "Tài khoản đã tồn tại");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            entity.setRole("user");
            usersRepository.save(entity);
            response.put("status", "success");
            response.put("message", "Đăng ký thành công");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/info")
    public ResponseEntity<?> GetInfo(@RequestBody users reqUsers) {
        Optional<users> u = usersRepository.findById(reqUsers.getId());
        if (u.isPresent()) {
            return ResponseEntity.ok(u.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin");
        }
    }

    @PostMapping("/searchFullname")
    public ResponseEntity<?> searchUserByName(@RequestBody users request) {
        List<users> users = usersRepository.findByFullnameContaining(request.getFullname());
        List<Map<String, Object>> ResultUserSearchByName = new ArrayList<>();
        if (!users.isEmpty()) {
            for (users u : users) {
                Optional<friend> f = friendRepository.getRelavtiveUser(request.getId(), u.getId());
                Map<String, Object> map = new HashMap<>();
                if (!f.isPresent()) {
                    map.put("id", u.getId());
                    map.put("fullname", u.getFullname());
                    map.put("relative", "Thêm bạn bè");
                } else {
                    map.put("id", u.getId());
                    map.put("fullname", u.getFullname());
                    map.put("relative", f.get().getRelative());
                }

                ResultUserSearchByName.add(map);
            }
            return ResponseEntity.ok(ResultUserSearchByName);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy");
        }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
        int userId = Integer.parseInt(payload.get("id"));
        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Mật khẩu mới không được để trống"));
        }

        Optional<users> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Người dùng không tồn tại");
        }

        users user = optionalUser.get();

        if (!user.getMk().equals(oldPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mật khẩu cũ không đúng");
        }

        if (oldPassword.equals(newPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Vui lòng đổi mật khẩu mới không trùng mật khẩu cũ"));
        }

        // Cập nhật mật khẩu mới
        user.setMk(newPassword);
        usersRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));

    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> TestSendEmail(@RequestBody users request) {
        try {
            String email = request.getEmail();
            String userName = request.getFullname();
            String token = UUID.randomUUID().toString();
            // Tạo dữ liệu cho email
            Map<String, Object> model = new HashMap<>();
            model.put("name", userName); // Truyền name vào template
            model.put("email", email); // Truyền email vào template (nếu cần)
            model.put("token", token);
            // Tạo đối tượng MailData
            MailData mailData = new MailData(
                    email, // emailToName: Địa chỉ email người nhận
                    "Xác minh tài khoản của bạn", // emailSubject: Chủ đề email
                    "verify-email", // templateName: Tên template Thymeleaf (verify-email.html)
                    model);

            // Gửi email
            emailService.sendMail(mailData);

            return ResponseEntity.ok("Gửi email xác minh thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
