package com.example.doan.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.atm;
import com.example.doan.Model.historyBalance;
import com.example.doan.Repository.HisBalanceRepo;
import com.example.doan.Repository.atmRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/Atm")
public class AtmController {
    @Autowired 
    private atmRepository atmRepository;
    @Autowired 
    private HisBalanceRepo hisBalanceRepo;

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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người chơi với ID: " + entity.getIdPlayer());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
        
    }
    @PostMapping("/get")    //Lấy ra tất cả thông tin về tiền của người dùng
    public ResponseEntity<?> getAtm (@RequestBody atm request){
        Optional<atm> atmInfo = atmRepository.findByIdPlayer(request.getIdPlayer());
        if (atmInfo.isPresent()) {
            return ResponseEntity.ok(atmInfo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy thông tin ATM");
        }
    }

    @PostMapping("/saveHis")
    public ResponseEntity<?> saveHis(@RequestBody historyBalance request){
        hisBalanceRepo.save(request);
        return ResponseEntity.ok(request);
    }
    @PostMapping("/search")
    public ResponseEntity<?> searchStk(@RequestBody atm entity) {
        Optional<atm>atm=atmRepository.findByStk(entity.getStk());
        return ResponseEntity.ok(atm);
    }

    
    @PostMapping("/calculate-reward")
public ResponseEntity<?> calculateReward(@RequestBody atm request) {
    String idPlayer = String.valueOf(request.getIdPlayer());

    // Tính tổng tiền nạp theo "trans"
    Float totalDeposit = hisBalanceRepo.sumTotalDepositByIdAndContent(idPlayer, "Nạp tiền");

    if (totalDeposit == null || totalDeposit == 0) {
        return ResponseEntity.ok("Người chơi chưa có giao dịch nạp tiền.");
    }

    // Ví dụ: nếu tổng nạp >= 1 triệu thì thưởng 2%
    float reward = 0;
    if (totalDeposit >= 1_000_000) {
        reward = totalDeposit * 0.02f;
    }

    if (reward > 0) {
        historyBalance bonusRecord = new historyBalance();
        bonusRecord.setPlayerId(Integer.parseInt(idPlayer));
        bonusRecord.setContent("Thưởng nạp tiền 5%");
        bonusRecord.setTrans((int) reward);
        bonusRecord.setBalance(0); // hoặc bạn có thể lấy số dư hiện tại nếu cần
        bonusRecord.setTimeChange(LocalDateTime.now().toString());
        hisBalanceRepo.save(bonusRecord);
    }

    Map<String, Object> response = new HashMap<>();
    response.put("idPlayer", idPlayer);
    response.put("totalDeposit", totalDeposit);
    response.put("reward", reward);
    response.put("message", reward > 0 ? "Đã cộng thưởng vào lịch sử" : "Không đủ điều kiện nhận thưởng");

    return ResponseEntity.ok(response);
}

}
