package edu.stanford.protege.webprotegeusermanagement.commands.dto;

import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotegeusermanagement.commands.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakUserRepository implements UserRepository {


    private final static Logger LOGGER = LoggerFactory.getLogger(KeycloakUserRepository.class);

    private final String realmName;
    private final Keycloak keycloak;

    public KeycloakUserRepository( @Value("${webprotege.keycloak.realmName}") String realmName, Keycloak keycloak) {
        this.realmName = realmName;
        this.keycloak = keycloak;
    }


    @Override
    public List<UserId> findUserIdsFromName(String name, boolean exact) {
        try {
            return keycloak.realm(realmName)
                    .users().search(name, exact).stream()
                    .map(userRepresentation -> new UserId(userRepresentation.getUsername()))
                    .collect(Collectors.toList());
        }catch (Exception e) {
            LOGGER.error("Error fetching users ", e);
            return new ArrayList<>();
        }
    }
}
