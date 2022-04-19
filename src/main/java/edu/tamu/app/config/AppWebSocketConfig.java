package edu.tamu.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

/**
 * Application Web Socket Configuration.
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
public class AppWebSocketConfig {

    /**
     * {@inheritDoc}
     */
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/channel");
        registry.setApplicationDestinationPrefixes("/ws");
        registry.setUserDestinationPrefix("/private");
    }

    /**
     * {@inheritDoc}
     */
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(16).maxPoolSize(Integer.MAX_VALUE);
    }

    /**
     * {@inheritDoc}
     */
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(Integer.MAX_VALUE);
        registration.setSendBufferSizeLimit(Integer.MAX_VALUE);
        registration.setSendTimeLimit(2 * 10 * 10000);
    }

    /**
     * {@inheritDoc}
     */
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect").setAllowedOrigins("*").withSockJS();
    }

}