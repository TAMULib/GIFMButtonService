/*
 * PersistedButtonRepo.java
 *
 * Version:
 *     $Id$
 *
 * Revisions:
 *     $Log$
 */
package edu.tamu.app.model.repo;

import org.springframework.stereotype.Repository;

import edu.tamu.app.model.PersistedButton;
import edu.tamu.weaver.data.model.repo.WeaverRepo;

/**
 * Repository for PersistedButton.
 *
 */
@Repository
public interface PersistedButtonRepo extends WeaverRepo<PersistedButton> {

}
