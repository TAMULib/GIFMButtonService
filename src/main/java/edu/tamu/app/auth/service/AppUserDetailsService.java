package edu.tamu.app.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import edu.tamu.app.auth.model.AppUserDetails;
import edu.tamu.app.model.User;
import edu.tamu.app.model.repo.UserRepo;
import edu.tamu.weaver.auth.service.AbstractWeaverUserDetailsService;

@Service
public class AppUserDetailsService extends AbstractWeaverUserDetailsService<User, UserRepo> {

    @Override
    public UserDetails buildUserDetails(User user) {
        return new AppUserDetails(user);
    }

}
