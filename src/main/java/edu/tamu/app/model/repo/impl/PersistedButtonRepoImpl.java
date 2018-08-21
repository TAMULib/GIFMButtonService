package edu.tamu.app.model.repo.impl;

import edu.tamu.app.model.PersistedButton;
import edu.tamu.app.model.repo.PersistedButtonRepo;
import edu.tamu.app.model.repo.PersistedButtonRepoCustom;
import edu.tamu.weaver.data.model.repo.impl.AbstractWeaverRepoImpl;

public class PersistedButtonRepoImpl extends AbstractWeaverRepoImpl<PersistedButton, PersistedButtonRepo> implements PersistedButtonRepoCustom {

    @Override
    protected String getChannel() {
        return "/channel/buttons";
    }
}
