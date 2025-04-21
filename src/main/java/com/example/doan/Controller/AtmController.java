package com.example.doan.Controller;

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
    @PostMapping("/createATM")
    public ResponseEntity<?> registerAtm(@RequestBody atm request) {
        try {
            // Tìm người chơi theo idPlayer
            Optional<atm> atmOpt = atmRepository.findByIdPlayer(request.getIdPlayer());
            
            if (atmOpt.isPresent()) {
                // Nếu tài khoản ATM đã tồn tại, cập nhật stk
                atm existingAtm = atmOpt.get();
                existingAtm.setStk(request.getStk());  // Cập nhật stk mới
                atm updatedAtm = atmRepository.save(existingAtm); // Lưu lại vào DB
                
                return ResponseEntity.ok(updatedAtm);  // Trả về tài khoản đã cập nhật
            } else {
                // Nếu chưa có tài khoản ATM, tạo mới
                atm newAtm = new atm(request.getIdPlayer(), request.getStk());
                atm savedAtm = atmRepository.save(newAtm);
                return ResponseEntity.ok(savedAtm);  // Trả về tài khoản mới tạo
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi xử lý yêu cầu: " + e.getMessage());
        }
        
    }

    
}
