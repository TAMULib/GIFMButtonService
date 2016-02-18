/* 
 * UserController.java 
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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Auth;
import edu.tamu.framework.aspect.annotation.Shib;
import edu.tamu.framework.model.ApiResponse;
import edu.tamu.framework.model.Credentials;

/**
 * User Controller
 * 
 */
@Controller
@ApiMapping("/user")
public class UserController {

    @Autowired
    private AppUserRepo userRepo;

    /**
     * Websocket endpoint to request credentials.
     * 
     * @param shibObj
     *            Object
     * 
     * @return ApiResponse
     * 
     * @throws Exception
     * 
     */
    @ApiMapping("/credentials")
    @Auth
    public ApiResponse credentials(@Shib Object shibObj) throws Exception {
        return new ApiResponse(SUCCESS, (Credentials) shibObj);
    }

    /**
     * Returns all users.
     * 
     * @return
     * @throws Exception
     */
    @ApiMapping("/all")
    @Auth(role = "ROLE_MANAGER")
    public ApiResponse allUsers() throws Exception {
        Map<String, List<AppUser>> map = new HashMap<String, List<AppUser>>();
        map.put("list", userRepo.findAll());
        return new ApiResponse(SUCCESS, map);
    }

}
