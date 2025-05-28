package com.example.doan.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.atm;
// import com.example.doan.Model.historyBalance;
import com.example.doan.Model.sessionPlayer;
import com.example.doan.Model.users;
import com.example.doan.Repository.HisBalanceRepo;
import com.example.doan.Repository.MessageRepo;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.atmRepository;
import com.example.doan.Repository.betHisfbxsRepo;
import com.example.doan.Repository.friendRepository;
import com.example.doan.Repository.sessionPlayerRepo;
import com.example.doan.Service.EmailService;
import com.example.doan.utils.MailData;

import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("admin")
@RestController
public class AdminController {
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
    @Autowired
    private EmailService emailService;

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hello")
    public ResponseEntity<?> Home(@RequestBody users body) {
        System.out.println(body.getTk());
        return ResponseEntity.ok(body);
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/allUsers")
    public ResponseEntity<?> getFullUsers() {
        List<users> users = usersRepository.findAllUsers();
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy");
        }
    }

    @PostMapping("/update") // Chinh sua ten va email
    public ResponseEntity<?> updateUser(@RequestBody users request) {
        Optional<users> userOpt = usersRepository.findById(request.getId());
        if (userOpt.isPresent()) {
            users user = userOpt.get();
            user.setFullname(request.getFullname());
            user.setEmail(request.getEmail());
            users updatedUser = usersRepository.save(user);
            return ResponseEntity.ok(updatedUser); // ✅ Trả về bản ghi vừa lưu
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user ID: " + request.getId());
        }
    }

    @PostMapping("/updateTkMK")
    public ResponseEntity<?> updateTkMK(@RequestBody users request) {
        Optional<users> userOpt = usersRepository.findById(request.getId());
        if (userOpt.isPresent()) {
            users user = userOpt.get();
            user.setTk(request.getTk());
            user.setMk(request.getMk());
            users updatedUser = usersRepository.save(user);
            return ResponseEntity.ok(updatedUser); // ✅ Trả về bản ghi vừa lưu
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy user ID: " + request.getId());
        }
    }

    @PostMapping("/updateBalan") // sửa số dư của người dùng
    public ResponseEntity<?> updateBalan(@RequestBody atm entity) {
        try {
            Optional<atm> atmInfo = atmRepository.findByIdPlayer(entity.getIdPlayer());
            if (atmInfo.isPresent()) {
                atm atm = atmInfo.get();
                atm.setBalance(entity.getBalance()); // Cập nhật số dư mới
                atmRepository.save(atm);
                return ResponseEntity.ok(entity);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy người chơi với ID: " + entity.getIdPlayer());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> SignUp(@RequestBody users entity) {
        Optional<users> userByTk = usersRepository.findByTkAndIsDelete(entity.getTk(), false);
        Optional<users> userByEmail = usersRepository.findByEmailAndIsDelete(entity.getEmail(), false);

        // Trường hợp tài khoản đã tồn tại và chưa bị xóa
        if (userByTk.isPresent() && !Boolean.TRUE.equals(userByTk.get().getIsDelete())) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại và đang hoạt động.");
        }

        if (userByEmail.isPresent() && !Boolean.TRUE.equals(userByEmail.get().getIsDelete())) {
            return ResponseEntity.badRequest().body("Email đã được sử dụng và đang hoạt động.");
        }

        // Nếu tài khoản bị xóa (is_delete = true) → ghi đè bản ghi
        users userToSave;
        if (userByTk.isPresent() && Boolean.TRUE.equals(userByTk.get().getIsDelete())) {
            userToSave = userByTk.get();
            userToSave.setMk(entity.getMk());
            userToSave.setFullname(entity.getFullname());
            userToSave.setEmail(entity.getEmail());
            userToSave.setIsDelete(false); // Khôi phục lại
        } else {
            userToSave = entity;
        }

        userToSave.setRole("user");
        userToSave.setIsVerify(true);
        usersRepository.save(userToSave);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    // xoa user
    @PutMapping("/delete")
    public ResponseEntity<?> softDeleteUser(@RequestBody users request) {
        int userId = request.getId();
        Map<String, String> response = new HashMap<>();

        Optional<users> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            response.put("message", "Không tìm thấy người dùng với ID: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            users user = optionalUser.get();
            user.setIsDelete(true);
            usersRepository.save(user);

            response.put("message", "Đã đánh dấu người dùng là đã xóa (isDelete = true)");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Lỗi khi cập nhật isDelete: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // tong tien thang
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/totalMoney")
    public ResponseEntity<?> totalMoney(@RequestBody sessionPlayer request) {
        try {

            Integer totalMoney = sessionPlayerRepo.sumBetWinAllGame(request.getPlayerId());
            if (totalMoney != null && totalMoney > 0) {
                return ResponseEntity.ok(totalMoney);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy người chơi với ID: " + request.getPlayerId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    @PutMapping("/restore")
    public ResponseEntity<?> restoreUser(@RequestBody users request) {
        int userId = request.getId();
        Map<String, String> response = new HashMap<>();

        Optional<users> optionalUser = usersRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            response.put("message", "Không tìm thấy người dùng với ID: " + userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            users user = optionalUser.get();
            user.setIsDelete(false);
            user.setIsActive(true);
            usersRepository.save(user);

            response.put("message", "Đã khôi phục tài khoản (isActive = true, isDelete = false)");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("message", "Lỗi khi khôi phục tài khoản: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // tong tien thua
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/totalLost")
    public ResponseEntity<?> totalLost(@RequestBody sessionPlayer request) {
        try {
            Integer totalLost = sessionPlayerRepo.sumBetLostAllGame(request.getPlayerId());
            if (totalLost != null && totalLost > 0) {
                return ResponseEntity.ok(totalLost);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy người chơi với ID: " + request.getPlayerId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/totalMoneyGame")
    public ResponseEntity<?> totalMoneyGame(@RequestBody sessionPlayer request) {
        try {
            Integer totalMoney = sessionPlayerRepo.sumRengWin(request.getPlayerId());
            System.out.println("ID Người chơi: " + request.getPlayerId());
            System.out.println("Tổng tiền thắng: " + totalMoney);
            if (totalMoney != null && totalMoney > 0) {
                return ResponseEntity.ok(totalMoney);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy người chơi với ID: " + request.getPlayerId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    // tongtien thua
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/totalLostGame")
    public ResponseEntity<?> totalLostGame(@RequestBody sessionPlayer request) {
        try {
            Integer totalLost = sessionPlayerRepo.sumRengLost(request.getPlayerId());
            if (totalLost != null && totalLost > 0) {
                return ResponseEntity.ok(totalLost);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy người chơi với ID: " + request.getPlayerId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    // Chẵn lẻ win
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/totalMoneyCL")
    public ResponseEntity<?> totalMoneyCL(@RequestBody sessionPlayer request) {
        try {
            Integer totalMoney = sessionPlayerRepo.sumClWin(request.getPlayerId());
            return ResponseEntity.ok(totalMoney != null ? totalMoney : 0);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    // Chẵn lẻ thua
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/totalLostCL")
    public ResponseEntity<?> totalLostCL(@RequestBody sessionPlayer request) {
        try {
            Integer totalLost = sessionPlayerRepo.sumClLose(request.getPlayerId());
            return ResponseEntity.ok(totalLost != null ? totalLost : 0);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    // dang ky stk
    // @PreAuthorize("hasRole('ADMIN')")


    @GetMapping("/getSumBetRengWin")
    public ResponseEntity<?> getSumBetRengWin() {
        try {
            Integer sumBetRengWin = sessionPlayerRepo.sumRengBetWin();
            return ResponseEntity.ok(sumBetRengWin != null ? sumBetRengWin : 0);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    @GetMapping("/getSumBetRengLose")
    public ResponseEntity<?> getSumBetRengLose() {
        try {
            Integer sumBetRengLose = sessionPlayerRepo.sumRengBetLose();
            return ResponseEntity.ok(sumBetRengLose != null ? sumBetRengLose : 0);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    @GetMapping("/getSumBetCLWin")
    public ResponseEntity<?> getSumBetCLWin() {
        try {
            Integer sumBetCLWin = sessionPlayerRepo.sumTXBetWin();
            return ResponseEntity.ok(sumBetCLWin != null ? sumBetCLWin : 0);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

    @GetMapping("/getSumBetCLLose")
    public ResponseEntity<?> getSumBetCLLose() {
        try {
            Integer sumBetCLLose = sessionPlayerRepo.sumTXBetLose();
            return ResponseEntity.ok(sumBetCLLose != null ? sumBetCLLose : 0);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
    }

}
