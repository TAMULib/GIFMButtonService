/*
 * UserRepo.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.User;
import edu.tamu.weaver.auth.model.repo.AbstractWeaverUserRepo;

/**
 * Application User repository.
 *
 */
@Repository
public interface UserRepo extends AbstractWeaverUserRepo<User>, UserRepoCustom {


    /**
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
     */
    @Override
    public void delete(User user);

}
