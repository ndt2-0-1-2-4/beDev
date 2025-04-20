package com.example.doan.Controller;

import java.util.*;
import com.example.doan.Repository.sessionPlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.doan.Model.historyBalance;
import com.example.doan.Model.sessionGame;
import com.example.doan.Model.sessionPlayer;
import com.example.doan.Repository.HisBalanceRepo;
import com.example.doan.Repository.sessionGameRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("game")
public class gameController {
    @Autowired
    private sessionPlayerRepo sessionPlayerRepo;
    @Autowired
    private sessionGameRepo sessionGameRepo;
    @Autowired
    private HisBalanceRepo hisBalanceRepo;

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
        List<Object[]> rawList = hisBalanceRepo.findTop5ByIdPlayer(idPlayer);

        // Tạo danh sách kết quả sau khi map
        ArrayList<historyBalance> listHis = new ArrayList<>();

        for (Object[] obj : rawList) {
            historyBalance balance = new historyBalance();

            // Giả sử thứ tự cột: id_player, timechange, content, trans, balance
            balance.setPlayerId((Integer) obj[0]);
            balance.setTimeChange((String) obj[1]);
            balance.setContent((String) obj[2]);
            balance.setTrans((Integer) obj[3]);
            balance.setBalance((Integer) obj[4]); // hoặc BigDecimal nếu bạn dùng kiểu đó

            listHis.add(balance);
        }

        // Trả về danh sách lịch sử
        return ResponseEntity.ok(listHis);

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
}
