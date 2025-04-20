package com.example.doan.ws;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.example.doan.Controller.gameController;
import com.example.doan.Model.users;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.socket.TextMessage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.springframework.http.HttpHeaders;
import java.util.ArrayList;

public class ClientInfoHandler extends TextWebSocketHandler {
    HttpSession httpSession ;
    HttpServletRequest request;
    private List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username"); // Lấy tên người dùng
        System.out.println("User  connected: " + username);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Client disconnected: " + (String)session.getAttributes().get("username"));
    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String username = (String) session.getAttributes().get("username");
        String msg=(String)message.getPayload();
        System.out.println(username+":"+msg);
    }

}
