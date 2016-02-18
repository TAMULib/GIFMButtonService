/* 
 * AppContextInitializedHandler.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import edu.tamu.framework.CoreContextInitializedHandler;
import edu.tamu.framework.model.repo.SymlinkRepo;

/**
 * Handler for when the servlet context refreshes.
 * 
 */
@Component
@EnableConfigurationProperties(SymlinkRepo.class)
class AppContextInitializedHandler extends CoreContextInitializedHandler {

    @Autowired
    ApplicationContext applicationContext;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void before(ContextRefreshedEvent event) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void after(ContextRefreshedEvent event) {

    }

}
