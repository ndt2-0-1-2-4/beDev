package com.example.doan.ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GameRRHandler extends TextWebSocketHandler {
    
    private List<WebSocketSession> sessions = new ArrayList<>();

    @Override 
    public void afterConnectionEstablished( WebSocketSession session) throws IOException{
        sessions.add(session);
        String username=(String)session.getAttributes().get("username");
        System.out.println("Client RR : " +username);
    }

    @Override 
    public void afterConnectionClosed(WebSocketSession session , CloseStatus status) throws IOException{
        sessions.remove(session);
    }

    @Override
    public void handleMessage( WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String username = (String) session.getAttributes().get("username");
        String msg = (String) message.getPayload();
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode= objectMapper.readTree(msg);
        String type= jsonNode.get("type").asText();
    }
}
