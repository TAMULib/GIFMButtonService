/* 
 * AppStompInterceptor.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller.interceptor;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.framework.interceptor.CoreStompInterceptor;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

/**
 * Application Stomp interceptor. Checks command, decodes and verifies token,
 * either returns error message to frontend or continues to controller.
 * 
 */
@Component
public class AppStompInterceptor extends CoreStompInterceptor {

    @Autowired
    private AppUserRepo userRepo;

    @Value("${app.authority.admins}")
    private String[] admins;

    @Autowired
    @Lazy
    private SimpMessagingTemplate simpMessagingTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public Credentials confirmCreateUser(Credentials shib) {

        AppUser user;
        String adminTarget;

        if (shib.getUin().equals("null")) {
            user = userRepo.findByEmail(shib.getEmail());
            adminTarget = shib.getEmail();

            if (user == null) {
                // do not create user
                // return null for the core interceptor to return error to ui
                return null;
            }
        } else {
            user = userRepo.findByUin(Long.parseLong(shib.getUin()));
            adminTarget = shib.getUin();
        }

        if (user == null) {

            if (shib.getRole() == null) {
                shib.setRole("ROLE_USER");
            }

            for (String uin : admins) {
                if (uin.equals(adminTarget)) {
                    shib.setRole("ROLE_ADMIN");
                }
            }

            user = new AppUser();

            if (!shib.getUin().equals("null")) {
                user.setUin(Long.parseLong(shib.getUin()));
            }

            user.setRole(shib.getRole());

            user.setFirstName(shib.getFirstName());
            user.setLastName(shib.getLastName());

            user.setEmail(shib.getEmail());

            user = userRepo.save(user);

            logger.info("Created new user: " + shib.getFirstName() + " " + shib.getLastName() + ")");

            Map<String, Object> userMap = new HashMap<String, Object>();

            userMap.put("list", userRepo.findAll());

            simpMessagingTemplate.convertAndSend("/channel/users", new ApiResponse(SUCCESS, userMap));
        }

        shib.setRole(user.getRole());

        return shib;
    }

}
