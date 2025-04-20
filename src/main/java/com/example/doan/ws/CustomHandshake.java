package com.example.doan.ws;

import org.springframework.http.HttpHeaders;

import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import jakarta.servlet.http.HttpSession;
public class CustomHandshake extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String username = request.getURI().getQuery(); // Lấy query string
        if (username != null && !username.isEmpty()) {
            // Tách tên người dùng từ query string
            String[] params = username.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "id".equals(keyValue[0])) {
                    attributes.put("id", keyValue[1]); // Lưu tên người dùng vào attributes
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,WebSocketHandler wsHandler, Exception exception) {
    }
}
