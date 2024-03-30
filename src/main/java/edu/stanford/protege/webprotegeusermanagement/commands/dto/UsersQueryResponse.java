package edu.stanford.protege.webprotegeusermanagement.commands.dto;

import edu.stanford.protege.webprotege.common.Response;
import edu.stanford.protege.webprotege.common.UserId;

import java.util.List;

public record UsersQueryResponse(List<UserId> userIds) implements Response {

}
