/* 
 * AppWebSocketConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import edu.tamu.framework.config.CoreWebSocketConfig;
import edu.tamu.app.controller.interceptor.AppStompInterceptor;

/**
 * Application Web Socket Configuration.
 * 
 */
@Configuration
@EnableWebSocketMessageBroker
public class AppWebSocketConfig extends CoreWebSocketConfig {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(appStompInterceptor());
    }

    /**
     * Stomp interceptor bean.
     * 
     * @return StompInterceptor
     * 
     */
    @Bean
    public AppStompInterceptor appStompInterceptor() {
        return new AppStompInterceptor();
    }

}