package cn.sbx0.space.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket配置
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        // 客户端就可以通过这个端点来进行连接
        stompEndpointRegistry.addEndpoint("/stomp")
                .setAllowedOrigins("*") //解决跨域问题
                .withSockJS(); // 开启SockJS支持
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端给服务端发消息的地址的前缀
        registry.setApplicationDestinationPrefixes("/web");
        // 用户 向 用户 点对点发送消息时 需要加"/chat"前缀
        registry.setUserDestinationPrefix("/chat");
        // 客户端接收服务端消息的地址的前缀
        registry.enableSimpleBroker("/channel");
    }

}