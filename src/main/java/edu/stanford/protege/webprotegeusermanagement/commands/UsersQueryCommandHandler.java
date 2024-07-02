package edu.stanford.protege.webprotegeusermanagement.commands;

import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotege.ipc.CommandHandler;
import edu.stanford.protege.webprotege.ipc.ExecutionContext;
import edu.stanford.protege.webprotege.ipc.WebProtegeHandler;
import edu.stanford.protege.webprotegeusermanagement.commands.dto.UsersQueryRequest;
import edu.stanford.protege.webprotegeusermanagement.commands.dto.UsersQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import javax.annotation.Nonnull;
import java.util.List;

@WebProtegeHandler
public class UsersQueryCommandHandler implements CommandHandler<UsersQueryRequest, UsersQueryResponse> {

    private final static Logger LOGGER = LoggerFactory.getLogger(UsersQueryRequest.class);

    private final UserRepository userRepository;

    public UsersQueryCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Nonnull
    @Override
    public String getChannelName() {
        return UsersQueryRequest.CHANNEL;
    }

    @Override
    public Class<UsersQueryRequest> getRequestClass() {
        return UsersQueryRequest.class;
    }

    @Override
    public Mono<UsersQueryResponse> handleRequest(UsersQueryRequest request, ExecutionContext executionContext) {
        LOGGER.info("Handle request");
        List<UserId> users = userRepository.findUserIdsFromName(request.userName(), request.exactMatch());
        return Mono.just(new UsersQueryResponse(users));
    }
}
