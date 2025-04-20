package com.example.doan.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.doan.Model.atm;
import com.example.doan.Model.betHisfbxs;
import com.example.doan.Model.betHisfbxs.BetType;
import com.example.doan.Repository.UsersRepository;
import com.example.doan.Repository.atmRepository;
import com.example.doan.Repository.betHisfbxsRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/betHisfbxs")
public class betHisfbxsController {
    
    @Autowired
    private betHisfbxsRepo betHisfbxsRepo;
    @Autowired
    private UsersRepository usersrepository;

    @Autowired
    private AtmController atmController;

    @Autowired
    private atmRepository atmRepository;


    @PersistenceContext
    private EntityManager em;

    @GetMapping("/check-table")
    public ResponseEntity<?> checkRawData() {
        List<Object[]> raw = em.createNativeQuery("SELECT * FROM betHisfbxs").getResultList();
        raw.forEach(row -> System.out.println(Arrays.toString(row)));
        return ResponseEntity.ok(raw);
}   

@GetMapping("/test-all")
public ResponseEntity<?> getAll() {
    List<betHisfbxs> list = betHisfbxsRepo.findAll();
    list.forEach(b -> System.out.println("-> idPlayer: " + b.getIdPlayer()));
    return ResponseEntity.ok(list);
}




    @PostMapping("getbetHisfbxs")
    public ResponseEntity<?> getbetHisfbxs(@RequestBody betHisfbxs request) {
    try {
        int idplayer = request.getIdPlayer();
        betHisfbxs.BetType betType = request.getBetType();

        List<betHisfbxs> listBetHisfbxs = betHisfbxsRepo.findByIdPlayerAndBetType(idplayer, betType);

        List<Map<String, Object>> result = new ArrayList<>();

        // Nếu là xổ số thì gọi API kết quả để xử lý reward động
        Set<String> last2Digits = new HashSet<>();
        if (betType == betHisfbxs.BetType.LOTTERY) {
            String apiUrl = "https://xoso188.net/api/front/open/lottery/history/list/5/miba";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());
                JsonNode issueList = root.path("t").path("issueList");

                if (!issueList.isEmpty()) {
                    String rawDetail = issueList.get(0).path("detail").asText();
                    JsonNode detailArray = mapper.readTree(rawDetail);

                    for (JsonNode node : detailArray) {
                        String[] parts = node.asText().split(",");
                        for (String num : parts) {
                            if (num.length() >= 2) {
                                last2Digits.add(num.substring(num.length() - 2));
                            } else {
                                last2Digits.add(num);
                            }
                        }
                    }
                }
            }
        }

        for (betHisfbxs bet : listBetHisfbxs) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", bet.getId());
            map.put("betAmount", bet.getBetAmount());
            map.put("multi", bet.getMulti());
            map.put("prediction", bet.getPrediction());
            map.put("referenceId", bet.getReferenceId());
            map.put("betTime", bet.getBetTime());
            map.put("status", bet.getStatus());
            map.put("betType", bet.getBetType());

            // Reward chỉ áp dụng cho xổ số
            if (betType == betHisfbxs.BetType.LOTTERY && bet.getStatus()) {
                if (last2Digits.contains(bet.getPrediction())) {
                    int reward = bet.getBetAmount() * bet.getMulti();
                    map.put("reward", reward);
                } else {
                    map.put("reward", 0);
                }
            }

            result.add(map);
        }

        return ResponseEntity.ok(result);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi lấy lịch sử cược.");
    }
}
    
    // cược bóng
    @PostMapping("/placeBet")
    public ResponseEntity<?> placeBet(@RequestBody betHisfbxs betRequest) {
        try {
            betRequest.setBetTime(LocalDateTime.now()); 
            System.out.println(">>> Dữ liệu nhận được: " + betRequest);
            betHisfbxsRepo.save(betRequest); 
            return ResponseEntity.ok(Map.of("message", "Đặt cược thành công!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Đặt cược thất bại!"));
        }
    }
    
    // cược xổ số
    @PostMapping("/placeLotteryBet")
    public ResponseEntity<?> placeLotteryBet(@RequestBody betHisfbxs betRequest) {
        try {
            betRequest.setBetTime(LocalDateTime.now());
            betRequest.setReferenceId(LocalDate.now().toString()); // reference_id là ngày cược
            betRequest.setStatus(false); // mặc định chưa xử lý
        
            System.out.println(">>> Đặt cược xổ số: " + betRequest);
            betHisfbxsRepo.save(betRequest);

            return ResponseEntity.ok(Map.of("message", "Đặt cược xổ số thành công!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Đặt cược xổ số thất bại!"));
        }
}

    // hàm trả thưởng football
    @GetMapping("/settleBets")
    public ResponseEntity<?> settleBets() {
        try {
            List<betHisfbxs> betsToSettle = betHisfbxsRepo.findByStatusFalseAndBetType(BetType.FOOTBALL);
            System.out.println("Số cược chưa xử lý: " + betsToSettle.size());
    
            String apiUrl = "https://api.football-data.org/v4/competitions/PL/matches";  
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Auth-Token", "17ee52ab7c3d494794f524ea8abff2f8");
    
            ResponseEntity<String> response = new RestTemplate().exchange(
                    apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    
            ObjectMapper mapper = new ObjectMapper();
            JsonNode matches = mapper.readTree(response.getBody()).get("matches");
            System.out.println("Số trận từ API: " + matches.size());
    
            for (betHisfbxs bet : betsToSettle) {
                System.out.println("Đang xử lý cược ID: " + bet.getId() + " - ReferenceId: " + bet.getReferenceId());
    
                boolean foundMatch = false;
    
                for (JsonNode match : matches) {
                    String matchId = match.get("id").asText();
                    String homeTeam = match.get("homeTeam").get("name").asText();
                    String awayTeam = match.get("awayTeam").get("name").asText();
                    String status = match.get("status").asText();
    
                    if (bet.getReferenceId().equals(matchId)) {
                        foundMatch = true;
    
                        System.out.println("Tìm thấy trận tương ứng: " + homeTeam + " vs " + awayTeam + " - Status: " + status);
    
                        if (!"FINISHED".equals(status)) {
                            System.out.println("Trận chưa kết thúc → bỏ qua");
                            break;
                        }
    
                        JsonNode scoreNode = match.get("score").get("fullTime");
                        int home = scoreNode.get("home").asInt();
                        int away = scoreNode.get("away").asInt();
                        String actualResult = String.format("%d-%d", home, away);
    
                        System.out.println("Kết quả trận đấu: " + actualResult + " | Dự đoán của người chơi: " + bet.getPrediction());
    
                        if (actualResult.equals(bet.getPrediction())) {
                            int reward = bet.getBetAmount() * bet.getMulti();
                            System.out.println(">> Dự đoán đúng! Trả thưởng: " + reward);
    
                            atmRepository.findByIdPlayer(bet.getIdPlayer()).ifPresent(atm -> {
                                atm.setBalance(atm.getBalance() + reward);
                                atmRepository.save(atm);
                            });
                        } else {
                            System.out.println(">> Dự đoán sai, không trả thưởng.");
                        }
    
                        bet.setStatus(true);
                        betHisfbxsRepo.save(bet);
                        System.out.println("Đã cập nhật status = true cho cược ID: " + bet.getId());
                        break;
                    }
                }
    
                if (!foundMatch) {
                    System.out.println("Không tìm thấy trận phù hợp với referenceId: " + bet.getReferenceId());
                }
            }
    
            return ResponseEntity.ok("Hoàn tất xử lý các cược đã kết thúc.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý cược.");
        }
    }

    // hàm trả thưởng xổ số
    @GetMapping("/settleLotteryBets")
    public ResponseEntity<?> settleLotteryBets() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);
            LocalTime now = LocalTime.now();
    
            LocalDateTime startTime = LocalDateTime.of(yesterday, LocalTime.of(19, 0));
            LocalDateTime endTime = LocalDateTime.of(today, LocalTime.of(18, 30));
    
            if (now.isBefore(endTime.toLocalTime())) {
                return ResponseEntity.ok("Chưa đến giờ xử lý (sau 18:30 mới xử lý).");
            }
    
            // Lấy cược chưa xử lý trong khoảng thời gian cần xét
            List<betHisfbxs> bets = betHisfbxsRepo.findByBetTypeAndStatusFalseAndBetTimeBetween(
                BetType.LOTTERY, startTime, endTime
            );
    
            if (bets.isEmpty()) return ResponseEntity.ok("Không có cược nào cần xử lý."); 
    
            // Gọi API kết quả xổ số
            String apiUrl = "https://xoso188.net/api/front/open/lottery/history/list/5/miba";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
    
            if (!response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không lấy được kết quả xổ số.");
            }
    
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode issueList = root.path("t").path("issueList");
    
            if (issueList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không có dữ liệu xổ số.");
            }
    
            // Lấy kết quả gần nhất
            String rawDetail = issueList.get(0).path("detail").asText();
            List<String> allResults = new ArrayList<>();
            JsonNode detailArray = mapper.readTree(rawDetail);
            for (JsonNode node : detailArray) {
                String[] parts = node.asText().split(",");
                allResults.addAll(Arrays.asList(parts));
            }
    
            Set<String> last2Digits = allResults.stream()
                .map(num -> num.length() >= 2 ? num.substring(num.length() - 2) : num)
                .collect(Collectors.toSet());
    
            // Trả thưởng
            for (betHisfbxs bet : bets) {
                String predicted = bet.getPrediction();
                boolean win = last2Digits.contains(predicted);
    
                if (win) {
                    int reward = bet.getBetAmount() * bet.getMulti();
                    atmRepository.findByIdPlayer(bet.getIdPlayer()).ifPresent(atm -> {
                        atm.setBalance(atm.getBalance() + reward);
                        atmRepository.save(atm);
                    });
                    System.out.println(">> Trúng thưởng! ID cược: " + bet.getId() + ", + " + reward + " VNĐ");
                } else {
                    System.out.println(">> Trượt! ID cược: " + bet.getId());
                }
    
                bet.setStatus(true);
                betHisfbxsRepo.save(bet);
            }
    
            return ResponseEntity.ok("Đã xử lý " + bets.size() + " cược xổ số.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi trả thưởng xổ số.");
        }
    }
    

}
