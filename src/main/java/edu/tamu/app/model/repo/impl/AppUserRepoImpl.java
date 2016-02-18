/* 
 * AppUserRepoImpl.java 
 * 
 * Version: 
 *     $Id$ 
 * 
 * Revisions: 
 *     $Log$ 
 */
package edu.tamu.app.model.repo.impl;

import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.app.model.AppUser;
import edu.tamu.app.model.repo.AppUserRepo;
import edu.tamu.app.model.repo.AppUserRepoCustom;

/**
 * 
 */
public class AppUserRepoImpl implements AppUserRepoCustom {

    @Autowired
    private AppUserRepo appUserRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public AppUser create(Long uin) {
        AppUser user = appUserRepo.findByUin(uin);
        if (user == null) {
            return appUserRepo.save(new AppUser(uin));
        }
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AppUser create(String email, String firstName, String lastName, String role) {
        AppUser user = appUserRepo.findByEmail(email);
        if (user == null) {
            return appUserRepo.save(new AppUser(email, firstName, lastName, role));
        }
        return user;
    }

}
