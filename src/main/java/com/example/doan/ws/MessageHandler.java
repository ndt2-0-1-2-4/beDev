package com.example.doan.ws;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.TextMessage;

import com.example.doan.Model.Message;
import com.example.doan.Repository.MessageRepo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageHandler extends TextWebSocketHandler {
    private final MessageRepo messageRepo;
    public MessageHandler(MessageRepo messageRepo) {
        this.messageRepo = messageRepo;
    }


    private List<WebSocketSession> sessions= new ArrayList<>();
    private Map <String, WebSocketSession> userSession = new HashMap<>();

    @Override 
    public void afterConnectionEstablished( WebSocketSession session) throws Exception {
        sessions.add(session);
        String username = (String) session.getAttributes().get("id");
        System.out.println("Hello userid: "+username);
        userSession.put(username, session);
    }
    @Override 
    public void afterConnectionClosed(WebSocketSession session,  CloseStatus status) throws Exception {
        sessions.remove(session);
        userSession.remove(session.getAttributes().get("id"));
    }

    @Override 
    public void handleMessage(WebSocketSession session,  WebSocketMessage<?> message) throws Exception {
        String idSender = (String) session.getAttributes().get("id");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode= objectMapper.readTree(message.getPayload().toString());
        String type = jsonNode.get("type").asText();

        if (type.equals("message")) {
            personalMess(jsonNode, idSender);
        }
    }

    public void personalMess(JsonNode jsonNode, String idSender) {
        int idReceiver = jsonNode.get("idReceiver").asInt();
        String content =jsonNode.get("content").asText();
        sendMessage(content, idReceiver,idSender);
    }
    public void sendMessage(String msg,int idReceiver,String idSender) {
        WebSocketSession session = userSession.get(Integer.toString(idReceiver));
        int idSenderTemp=Integer.parseInt(idSender);
        if(session != null) {
            try {
                session.sendMessage(new TextMessage(msg));
            } catch (IOException e) { 
                e.printStackTrace();
            }
        }
        Message message = new Message();
        message.setIdMy(idSenderTemp);
        message.setIdFriend(idReceiver);
        message.setContent(msg);
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        message.setTimeSend(formattedDateTime);
        messageRepo.save(message);
    }
}
