/* 
 * LogbackConfig.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.config;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.ext.spring.ApplicationContextHolder;

/**
 * 
 */
@Configuration
public class LogbackConfig {

    @Value("${logging.encoder.pattern}")
    private String encoderPattern;

    @Value("${logging.file}")
    private String loggingFile;

    @Value("${logging.rolling.pattern}")
    private String rollingPattern;

    @Value("${logging.rolling.file-size}")
    private String rollingFileSize;

    @Value("${logging.rolling.threshold-level}")
    private String rollingThresholdLevel;

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    /**
     * 
     * @return
     */
    @Bean
    public LoggerContext loggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    /**
     * 
     * @param ctx
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public PatternLayoutEncoder encoder(LoggerContext ctx) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(ctx);
        encoder.setPattern(encoderPattern);
        return encoder;
    }

    /**
     * 
     * @param ctx
     * @param encoder
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public ConsoleAppender consoleAppender(LoggerContext ctx, PatternLayoutEncoder encoder) {
        ConsoleAppender appender = new ConsoleAppender();
        appender.setContext(ctx);
        appender.setEncoder(encoder);
        ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel("DEBUG");
        appender.addFilter(thresholdFilter);
        return appender;
    }

    /**
     * 
     * @param ctx
     * @param encoder
     * @return
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public RollingFileAppender rollingFileAppender(LoggerContext ctx, PatternLayoutEncoder encoder) {
        RollingFileAppender appender = new RollingFileAppender();
        appender.setFile(loggingFile);
        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setParent(appender);
        rollingPolicy.setFileNamePattern(rollingPattern);
        appender.setRollingPolicy(rollingPolicy);
        SizeBasedTriggeringPolicy triggeringPolicy = new SizeBasedTriggeringPolicy();
        triggeringPolicy.setMaxFileSize(rollingFileSize);
        appender.setTriggeringPolicy(triggeringPolicy);
        ThresholdFilter thresholdFilter = new ThresholdFilter();
        thresholdFilter.setLevel(rollingThresholdLevel);
        appender.addFilter(thresholdFilter);
        appender.setContext(ctx);
        appender.setEncoder(encoder);
        return appender;
    }

}
