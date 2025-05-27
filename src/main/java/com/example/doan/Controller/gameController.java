package com.example.doan.Controller;

import java.util.*;
import com.example.doan.Repository.sessionPlayerRepo;
import com.example.doan.ws.GameCLHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.doan.Model.historyBalance;
import com.example.doan.Model.sessionGame;
import com.example.doan.Model.sessionPlayer;
import com.example.doan.Repository.HisBalanceRepo;
import com.example.doan.Repository.sessionGameRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("game")
public class gameController {
    @Autowired
    private sessionPlayerRepo sessionPlayerRepo;
    @Autowired
    private sessionGameRepo sessionGameRepo;
    @Autowired
    private HisBalanceRepo hisBalanceRepo;
    @Autowired
    private GameCLHandler gameCLHandler;

    @PostMapping("/getHistoryCl")
    public ResponseEntity<?> saveSession(@RequestBody sessionGame entity) {
        ArrayList<sessionGame> listHis = new ArrayList<>();
        listHis = sessionGameRepo.findTop10ByNamegameOrderByIdDesc(entity.getNamegame());
        return ResponseEntity.ok(listHis);
    }

    @PostMapping("/savePlayerHis")
    public ResponseEntity<?> savePlayerHis(@RequestBody sessionPlayer entity) {
        sessionPlayerRepo.save(entity);
        return ResponseEntity.ok(entity);
    }

    @PostMapping("/getPlayerHis")
    public ResponseEntity<?> getPlayerHis(@RequestBody historyBalance entity) {
        int idPlayer = entity.getIdPlayer();
        System.out.println("ID PLAYER NHẬN ĐƯỢC: " + idPlayer);
        // Lấy danh sách từ repo
        List<historyBalance> rawList = hisBalanceRepo.findTop5ByIdPlayer(idPlayer);
        // Trả về danh sách lịch sử
        return ResponseEntity.ok(rawList);

    }
    @PostMapping("/getPlayerHisAll")
    public ResponseEntity<?> getPlayerHisAll(@RequestBody sessionPlayer entity) {
        int idPlayer = entity.getPlayerId();
        System.out.println("ID PLAYER NHẬN ĐƯỢC: " + idPlayer);

        // Lấy danh sách từ repo
        List<sessionPlayer> rawList = sessionPlayerRepo.findTop5ByIdPlayer(idPlayer);

        // Trả về danh sách lịch sử
        return ResponseEntity.ok(rawList);
    }

    @PostMapping("/force")
    public ResponseEntity<String> forceGameResult(@RequestParam int code) {
        gameCLHandler.forceResult(code); 
        String msg = "Kết quả kế tiếp sẽ là: " + (code == 1 ? "xỉu" : code == 2 ? "tài" : "random");
        System.out.println(msg); 
        return ResponseEntity.ok(msg);
    }
}
