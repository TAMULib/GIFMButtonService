/* 
 * AppWebMvcConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import edu.tamu.framework.config.CoreWebMvcConfig;

/**
 * Application Web Mvc Configuration.
 */
@Configuration
@AutoConfigureAfter(DispatcherServletAutoConfiguration.class)
public class AppWebMvcConfig extends CoreWebMvcConfig {

    /**
     * 
     */
    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

}