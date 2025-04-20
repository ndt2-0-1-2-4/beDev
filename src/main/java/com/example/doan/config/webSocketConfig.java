package com.example.doan.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.example.doan.Repository.MessageRepo;
import com.example.doan.Repository.sessionGameRepo;
import com.example.doan.Repository.sessionPlayerRepo;
import com.example.doan.ws.ClientInfoHandler;
import com.example.doan.ws.CustomHandshake;
import com.example.doan.ws.GameCLHandler;
import com.example.doan.ws.GameRRHandler;
import com.example.doan.ws.MessageHandler;

@Configuration
@EnableWebSocket
public class webSocketConfig implements WebSocketConfigurer {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired 
    private sessionGameRepo sessionGameRepo;
    @Autowired 
    private sessionPlayerRepo sessionPlayerRepo;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ClientInfoHandler(), "")
                .addInterceptors(new CustomHandshake())
                .setAllowedOrigins("*") ;           
        registry.addHandler(new GameCLHandler(sessionGameRepo,sessionPlayerRepo), "/game/cl")     
                .addInterceptors(new CustomHandshake())
                .setAllowedOrigins("*");
        registry.addHandler(new GameRRHandler(), "/game/rr")     
            .addInterceptors(new CustomHandshake())
            .setAllowedOrigins("*");
        registry.addHandler(new MessageHandler(messageRepo), "/mess")
            .addInterceptors(new CustomHandshake())
            .setAllowedOrigins("*");
    }

}
