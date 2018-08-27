/*
 * UserRepoCustom.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
package edu.tamu.app.model.repo;

import edu.tamu.app.model.User;

/**
 *
 */
public interface UserRepoCustom {

    /**
     * method to delete application user
     *
     * @param user
     *            AppUser
     */
    public void delete(User user);

}
