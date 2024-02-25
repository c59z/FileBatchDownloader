package com.yuki.Socket.Config;

import com.yuki.Socket.Handler.FileDownloadProgressHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new FileDownloadProgressHandler(), "/DownloadFile")
                .setAllowedOrigins("*"); // 允许来自任何源的连接
    }


//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(myHandler(), "/DownloadFile")
//                .setAllowedOrigins("*")
//                .withSockJS();
//    }
//
//    @Bean
//    public WebSocketHandler myHandler() {
//        return new FileDownloadProgressHandler();
//    }

}
