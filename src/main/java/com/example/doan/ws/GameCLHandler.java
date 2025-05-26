package com.example.doan.ws;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.doan.Model.sessionGame;
import com.example.doan.Repository.sessionGameRepo;
import com.example.doan.Repository.sessionPlayerRepo;
import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;
@Component
public class GameCLHandler extends TextWebSocketHandler {

    private final sessionGameRepo sessionGameRepo;
    private final sessionPlayerRepo sessionplayerRepo;
    public GameCLHandler(sessionGameRepo sessionGameRepo,sessionPlayerRepo sessionPlayerRepo ) {
        this.sessionGameRepo = sessionGameRepo;
        this.sessionplayerRepo = sessionPlayerRepo;
    }

    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private int countdown = 15;
    private int wait=14;
    private Timer timer;
    private boolean isRecived = true;
    private boolean isStart = false;
    private int totalMoneyT=0;
    private int totalMoneyX=0;
    private HashMap<String ,String > GuessClient= new HashMap<>();
    private HashMap<String ,Integer > MoneyClient= new HashMap<>();


    @Override 
    public void afterConnectionEstablished( WebSocketSession session) throws Exception {
        sessions.add(session);
        session.sendMessage(new TextMessage(Integer.toString(countdown)));
        String idUser = (String) session.getAttributes().get("id");
        String guess = "";int money = 0;
        if(GuessClient.get(idUser.toString())!=null){
            guess=GuessClient.get(idUser.toString());
            money=MoneyClient.get(idUser.toString());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<Object,Object> result= new HashMap<>();
        result.put("type", "infobet");
        result.put("money",money );
        result.put("guess",guess );
        String jsonMessage = objectMapper.writeValueAsString(result);
        session.sendMessage(new TextMessage(jsonMessage));
        System.out.println("User Chan le connected: " + idUser );
        if (!isStart){
            startCountdown();
            isStart = true;
        }
    }    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Client disconnected: " + (String)session.getAttributes().get("id"));
    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String idUser = (String) session.getAttributes().get("id");
        String msg = (String) message.getPayload();
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode= objectMapper.readTree(msg);
        String type= jsonNode.get("type").asText();

        if(type.equals("sumbet")){
            handleBetMsg(jsonNode);
        }
        else if(type.equals("bet")){
            handleChoiceMsg(jsonNode, idUser);
        }
        else if(type.equals("changeRs")){
            forceResult(jsonNode.get("code").asInt());
        }
    }

    private void handleBetMsg(JsonNode jsonNode) throws Exception{
    }
    private void handleChoiceMsg(JsonNode jsonNode,String idUser) throws Exception{
        String choice =jsonNode.get("choice").asText();
        int money = jsonNode.get("money").asInt();
        if(choice.equals("tai")){
            totalMoneyX+=money;
        }
        else{
            totalMoneyT+=money;
        }
        System.out.println(MoneyClient.get(idUser));
        int tempMoney;
        if(MoneyClient.get(idUser)!=null){
           tempMoney=MoneyClient.get(idUser)+money;
        }
        else{
            tempMoney=money;
        }
        GuessClient.put(idUser, choice);
        MoneyClient.put(idUser, tempMoney);
        System.out.println(idUser+" : " + choice +"->" + tempMoney);
    }
    private void sendTotalMoney(int timeCountdown) throws IOException{
        String totalMoney=timeCountdown + ":" +totalMoneyT+":"+totalMoneyX;
        convertToJson("money", totalMoney);
    }
    private void convertToJson(String type,String message) throws IOException{
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("message", message);
        String jsonResult = objectMapper.writeValueAsString(result);
        sendMessageToAll(jsonResult);
    }
    private void startCountdown() throws IOException {
        
        GuessClient.clear();
        MoneyClient.clear();
        totalMoneyT =0;
        totalMoneyX=0;
        timer = new Timer();
        isRecived =true;
        convertToJson("start","");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (countdown >= 0) {
                    try {
                        sendTotalMoney(countdown);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    countdown--;
                } 
                else {
                    isRecived=false;
                    try {
                        ShowRs();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timer.cancel();
                    timer.purge();
    
                    Timer delayTimer = new Timer();
                    delayTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            countdown = 15;
                            try {
                                startCountdown();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }, wait*1000); 
                }
            }
        }, 0, 1000);
    } 

    // chinh kq txiu
    private int forcedCode = 0; // 0:random, 1: xỉu, 2: tài
    public void forceResult(int code) {
        this.forcedCode = code;
    }
    
    private List<Integer> generateDiceForResult(String expectedResult) {
        Random random = new Random();
        System.out.println(expectedResult);
        while (true) {
            int d1 = random.nextInt(1, 7);
            int d2 = random.nextInt(1, 7);
            int d3 = random.nextInt(1, 7);
            int sum = d1 + d2 + d3;
            if ((expectedResult.equals("tai") && sum > 10) || (expectedResult.equals("xiu") && sum <= 10)) {
                return Arrays.asList(d1, d2, d3);
            }
        }
    }

    public void ShowRs() throws IOException{
        List<Integer> resultInt;
        System.out.println(forcedCode);
        if (forcedCode == 1) {
        resultInt = generateDiceForResult("xiu");
        } else if (forcedCode == 2) {
            resultInt = generateDiceForResult("tai");
        } else {
            Random random = new Random();
            resultInt = Arrays.asList(
                random.nextInt(1, 7),
                random.nextInt(1, 7),
                random.nextInt(1, 7)
            );
        }

        forcedCode = 0; // Reset về random sau khi sử dụng

        int sum = resultInt.stream().mapToInt(Integer::intValue).sum();
        String resultString = resultInt.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(", "));
        

        sessionGame sessionGame = new sessionGame();
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        sessionGame.setResult(resultString);
        sessionGame.setTimeoccurs(formattedDateTime);
        sessionGame.setNamegame("Tài xỉu");

        sessionGameRepo.save(sessionGame);
        String result= "";
        if(sum >10){
            convertToJson("end",resultString);
            result ="tai";
        }
        else{
            convertToJson("end",resultString);
            result="xiu";
        }
        for (WebSocketSession i:sessions){
            String clientId = (String) i.getAttributes().get("id");
            if (GuessClient.get(clientId) !=null){
                ObjectMapper objectMapper = new ObjectMapper();
                HashMap<String, Object> rsMsg = new HashMap<>();
                rsMsg.put("type", "reward");
                if (GuessClient.get(clientId).equals(result)){  //Người chơi win
                    rsMsg.put("reward", MoneyClient.get(clientId));
                } 
                else{
                    rsMsg.put("reward",0);
                }
                rsMsg.put("result", resultInt);
                rsMsg.put("bet", MoneyClient.get(clientId));
                rsMsg.put("choice",GuessClient.get(clientId));
                String jsonResult = objectMapper.writeValueAsString(rsMsg);
                i.sendMessage(new TextMessage(jsonResult));
            }
        }
    }
    private void sendMessageToAll(String message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}

