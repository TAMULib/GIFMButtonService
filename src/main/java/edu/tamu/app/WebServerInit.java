/* 
 * WebServerInit.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import edu.tamu.framework.CoreSpringBanner;

/**
 * Web server initialization.
 * 
 */
@ComponentScan(basePackages = { "edu.tamu.framework", "edu.tamu.app" })
@SpringBootApplication
public class WebServerInit extends SpringBootServletInitializer {

    /**
     * Entry point to the application from within servlet.
     *
     * @param args
     *            String[]
     *
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(WebServerInit.class);
        application.setBanner(new CoreSpringBanner());
        application.run(args);
    }

    /**
     * Entry point to the application if run using spring-boot:run.
     *
     * @param application
     *            SpringApplicationBuilder
     *
     * @return SpringApplicationBuilder
     *
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.banner(new CoreSpringBanner());
        return application.sources(WebServerInit.class);
    }

}