package edu.stanford.protege.webprotegeusermanagement.commands;

import edu.stanford.protege.webprotege.common.UserId;

import java.util.List;

public interface UserRepository {

    List<UserId> findUserIdsFromName(String name, boolean exactMatch);
}
