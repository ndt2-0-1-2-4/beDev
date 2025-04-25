package com.example.doan.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.atm;
import com.example.doan.Model.historyBalance;
import com.example.doan.Repository.HisBalanceRepo;
import com.example.doan.Repository.atmRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/Atm")
public class AtmController {
    @Autowired
    private atmRepository atmRepository;
    @Autowired
    private HisBalanceRepo hisBalanceRepo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostMapping("/updateBalance") // trừ tiền ở số dư của người dùng
    public ResponseEntity<?> upadateBalan(@RequestBody atm entity) {
        try {
            Optional<atm> atmInfo = atmRepository.findByIdPlayer(entity.getIdPlayer());
            if (atmInfo.isPresent()) {
                atm atm = atmInfo.get();
                atm.setBalance(atm.getBalance() + entity.getBalance());
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

    @PostMapping("/get") // Lấy ra tất cả thông tin về tiền của người dùng
    public ResponseEntity<?> getAtm(@RequestBody atm request) {
        Optional<atm> atmInfo = atmRepository.findByIdPlayer(request.getIdPlayer());
        if (atmInfo.isPresent()) {
            return ResponseEntity.ok(atmInfo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin ATM");
        }
    }

    @PostMapping("/saveHis")
    public ResponseEntity<?> saveHis(@RequestBody historyBalance request) {
        hisBalanceRepo.save(request);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchStk(@RequestBody atm entity) {
        Optional<atm> atm = atmRepository.findByStk(entity.getStk());
        return ResponseEntity.ok(atm);
    }
    @PostMapping("/createATM")
    public ResponseEntity<?> registerAtm(@RequestBody atm request) {
        try {
            // Tìm người chơi theo idPlayer
            Optional<atm> atmOpt = atmRepository.findByIdPlayer(request.getIdPlayer());
            int balance = 5000;
            request.setBalance(balance); // Đặt số dư mặc định là 5000

            if (atmOpt.isPresent()) {
                // Nếu tài khoản ATM đã tồn tại, cập nhật stk
                atm existingAtm = atmOpt.get();
                existingAtm.setStk(request.getStk()); // Cập nhật stk mới
                atm updatedAtm = atmRepository.save(existingAtm); // Lưu lại vào DB

                return ResponseEntity.ok(updatedAtm); // Trả về tài khoản đã cập nhật
            } else {
                // Nếu chưa có tài khoản ATM, tạo mới
                atm newAtm = new atm(request.getIdPlayer() , request.getBalance(), request.getStk());
                atm savedAtm = atmRepository.save(newAtm);
                return ResponseEntity.ok(savedAtm); // Trả về tài khoản mới tạo
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }

    }
    @GetMapping("/getDailyClosingBalance")
    public ResponseEntity<?> getDailyClosingBalance(
            @RequestParam int playerId,
            @RequestParam String startDate) { // Ngày bắt đầu từ client

        try {
            // Parse ngày bắt đầu từ client
            LocalDate endDate = LocalDate.parse(startDate); // endDate là ngày mới nhất
            LocalDate startDay = endDate.minusDays(6); // Lấy 7 ngày (endDate - 6 ngày)

            // Tạo danh sách để lưu kết quả 7 ngày
            List<Map<String, Object>> weeklyBalances = new ArrayList<>();

            // Lặp qua từ startDay đến endDate (7 ngày)
            for (LocalDate currentDate = startDay; !currentDate.isAfter(endDate); currentDate = currentDate
                    .plusDays(1)) {

                String startOfDay = currentDate.atStartOfDay().format(formatter);
                String endOfDay = currentDate.atTime(LocalTime.MAX).format(formatter);

                List<historyBalance> dailyBalances = hisBalanceRepo.findDailyBalancesByPlayer(
                        playerId,
                        startOfDay,
                        endOfDay);

                Map<String, Object> dailyResponse = new HashMap<>();
                dailyResponse.put("date", currentDate.toString());

                if (dailyBalances.isEmpty()) {
                    dailyResponse.put("message", "Không tìm thấy giao dịch");
                    dailyResponse.put("hasData", false);
                } else {
                    historyBalance closingBalance = dailyBalances.get(0); // Giao dịch cuối ngày
                    dailyResponse.put("closingBalance", closingBalance.getBalance());
                    dailyResponse.put("lastTransactionTime", closingBalance.getTimeChange());
                    dailyResponse.put("content", closingBalance.getContent());
                    dailyResponse.put("hasData", true);
                }

                weeklyBalances.add(dailyResponse);
            }

            // Sắp xếp theo thứ tự ngày giảm dần (mới nhất đầu tiên)
            weeklyBalances.sort((a, b) -> LocalDate.parse((String) b.get("date"))
                    .compareTo(LocalDate.parse((String) a.get("date"))));

            Map<String, Object> response = new HashMap<>();
            response.put("playerId", playerId);
            response.put("startDate", startDay.toString()); // Ngày xa nhất
            response.put("endDate", endDate.toString()); // Ngày gần nhất
            response.put("dailyBalances", weeklyBalances);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi xử lý yêu cầu");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);    
        }
    }

    // Lấy lịch sử nạp tiền cuối mỗi ngày trong vòng 1 tuần trở lại
    @GetMapping("/getDailyRecharge")
    public ResponseEntity<?> getDailyrecharges(
            @RequestParam int playerId,
            @RequestParam String endDateStr) {

        try {
            // Parse ngày kết thúc từ client
            LocalDate endDate = LocalDate.parse(endDateStr);
            LocalDate startDate = endDate.minusDays(6); // Lấy 7 ngày (endDate - 6 ngày)

            // Tạo danh sách để lưu kết quả 7 ngày
            List<Map<String, Object>> dailyrecharges = new ArrayList<>();

            // Lặp qua từ startDate đến endDate (7 ngày)
            for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate); currentDate = currentDate
                    .plusDays(1)) {
                String startOfDay = currentDate.atStartOfDay().format(formatter);
                String endOfDay = currentDate.atTime(LocalTime.MAX).format(formatter);

                // có phương thức findDailyrechargesByPlayer để lấy tất cả giao dịch nạp tiền
                // trong ngày
                List<historyBalance> recharges = hisBalanceRepo.findDailyRechargeByPlayer(
                        playerId,
                        startOfDay,
                        endOfDay);

                Map<String, Object> dailyResponse = new HashMap<>();
                dailyResponse.put("date", currentDate.toString());

                if (recharges.isEmpty()) {
                    dailyResponse.put("totalRecharge", 0);
                    dailyResponse.put("hasData", false);
                } else {
                    // Tính tổng số tiền nạp trong ngày
                    double totalRecharge = recharges.stream()
                            .mapToDouble(historyBalance::getBalance)
                            .sum();

                    dailyResponse.put("totalRecharge", totalRecharge);
                    dailyResponse.put("transactionCount", recharges.size());
                    dailyResponse.put("hasData", true);
                }

                dailyrecharges.add(dailyResponse);
            }

            // Sắp xếp theo thứ tự ngày giảm dần (mới nhất đầu tiên)
            dailyrecharges.sort((a, b) -> LocalDate.parse((String) b.get("date"))
                    .compareTo(LocalDate.parse((String) a.get("date"))));

            Map<String, Object> response = new HashMap<>();
            response.put("playerId", playerId);
            response.put("startDate", startDate.toString()); // Ngày xa nhất
            response.put("endDate", endDate.toString()); // Ngày gần nhất
            response.put("dailyrecharges", dailyrecharges);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Lỗi khi xử lý yêu cầu");
            errorResponse.put("details", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/calculate-reward")
public ResponseEntity<?> calculateReward(@RequestBody atm request) {
    Integer idPlayer = request.getIdPlayer();
    System.out.println("Nhận idPlayer: " + idPlayer);

    Float totalDeposit = hisBalanceRepo.sumTotalDepositByIdAndContentLike(idPlayer, "%Nạp tiền%");

    if (totalDeposit == null) {
        totalDeposit = 0f;
    }

    int[][] rewardLevels = {
        {200_000_000, 1_579_000},
        {100_000_000,   879_000},
        {50_000_000,    360_000},
        {10_000_000,    100_000},
        {2_000_000,      40_000}
    };

    Integer currentBalance = atmRepository.findBalanceByIdPlayer(idPlayer);
    int rewarded = 0;

    for (int[] level : rewardLevels) {
        int threshold = level[0];
        int reward = level[1];

        if (totalDeposit >= threshold && !hisBalanceRepo.hasReceivedReward(idPlayer, reward)) {
            // Ghi nhận thưởng
            historyBalance bonusRecord = new historyBalance();
            bonusRecord.setPlayerId(idPlayer);
            bonusRecord.setContent("Thưởng nạp tiền");
            bonusRecord.setTrans(reward);
            bonusRecord.setBalance(currentBalance != null ? currentBalance + reward : reward);
            bonusRecord.setTimeChange(LocalDateTime.now().format(formatter));
            hisBalanceRepo.save(bonusRecord);

            // Cộng vào bảng atm
            Optional<atm> existingAtmOpt = atmRepository.findByIdPlayer(idPlayer);
            if (existingAtmOpt.isPresent()) {
                atm existingAtm = existingAtmOpt.get();
                existingAtm.setBalance(existingAtm.getBalance() + reward);
                atmRepository.save(existingAtm);
            }

            rewarded = reward;
            break; // chỉ cộng 1 lần, ở mốc cao nhất
        }
    }

    Map<String, Object> response = new HashMap<>();
    response.put("idPlayer", idPlayer);
    response.put("totalDeposit", totalDeposit);
    response.put("reward", rewarded);
    response.put("message", rewarded > 0 ? "Đã cộng thưởng vào lịch sử" : "Không đủ điều kiện nhận thêm thưởng");

    return ResponseEntity.ok(response);
}

}
