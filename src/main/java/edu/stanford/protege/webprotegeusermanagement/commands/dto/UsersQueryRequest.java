package edu.stanford.protege.webprotegeusermanagement.commands.dto;

import edu.stanford.protege.webprotege.common.Request;

public record UsersQueryRequest(String userName) implements Request<UsersQueryResponse> {

    public final static String CHANNEL = "webprotege.usersquery.QueryUsers";

    @Override
    public String getChannel() {
        return CHANNEL;
    }


}
