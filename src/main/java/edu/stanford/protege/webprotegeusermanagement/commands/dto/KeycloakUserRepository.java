package edu.stanford.protege.webprotegeusermanagement.commands.dto;

import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotegeusermanagement.commands.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
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

    /**
     * WebProtégé's application-level user id.  The realm's protocol mapper puts this
     * attribute into the {@code preferred_username} claim, and the gateway derives the
     * session {@code UserId} from that claim - so this attribute, not the Keycloak
     * username, is the identity that permissions must be granted against.  For accounts
     * created through registration the SPI sets it equal to the Keycloak username; for
     * legacy migrated accounts it holds the original WebProtégé user name.
     */
    public static final String WEBPROTEGE_USERNAME_ATTRIBUTE = "webprotege_username";

    private final String realmName;
    private final Keycloak keycloak;

    public KeycloakUserRepository( @Value("${webprotege.keycloak.realmName}") String realmName, Keycloak keycloak) {
        this.realmName = realmName;
        this.keycloak = keycloak;
    }


    @Override
    public List<UserId> findUserIdsFromName(String name, boolean exactMatch) {
        try {
            // briefRepresentation=false so the results carry user attributes; the client
            // has no overload combining it with exact matching, so exactness is applied
            // on the username here (matching Keycloak's case-insensitive exact search).
            return keycloak.realm(realmName)
                    .users().search(name, null, null, false).stream()
                    .filter(user -> !exactMatch || user.getUsername().equalsIgnoreCase(name))
                    .map(KeycloakUserRepository::toUserId)
                    .collect(Collectors.toList());
        }catch (Exception e) {
            LOGGER.error("Error fetching users ", e);
            return new ArrayList<>();
        }
    }

    private static UserId toUserId(UserRepresentation user) {
        var attributes = user.getAttributes();
        if (attributes != null) {
            var values = attributes.get(WEBPROTEGE_USERNAME_ATTRIBUTE);
            if (values != null && !values.isEmpty() && values.get(0) != null && !values.get(0).isBlank()) {
                return new UserId(values.get(0));
            }
        }
        return new UserId(user.getUsername());
    }
}
