/* 
 * AdminController.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.model.ApiResponse;

/**
 * Admin Controller.
 * 
 */
@RestController
@MessageMapping("/admin")
public class AdminController {

    /**
     * Websocket endpoint to request to broadcast message.
     * 
     * @param data
     *            String
     * @return ApiResponse
     * 
     * @throws Exception
     * 
     */
    @MessageMapping("/broadcast")
    @SendTo("/channel/admin/broadcast")
    @Auth(role = "ROLE_ADMIN")
    public ApiResponse broadcast(@Data String data) throws Exception {
        Map<String, String> messageMap = new HashMap<String, String>();
        messageMap.put("message", data);
        return new ApiResponse(SUCCESS, messageMap);
    }

}