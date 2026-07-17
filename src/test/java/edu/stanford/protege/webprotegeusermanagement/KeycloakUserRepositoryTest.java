package edu.stanford.protege.webprotegeusermanagement;


import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotegeusermanagement.commands.dto.KeycloakUserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeycloakUserRepositoryTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private RealmResource resource;

    @Mock
    private UsersResource userResource;

    private KeycloakUserRepository repository;


    @Before
    public void setUp() {
        when(keycloak.realm(eq("webprotege"))).thenReturn(resource);
        when(resource.users()).thenReturn(userResource);
        repository = new KeycloakUserRepository("webprotege", keycloak);
    }

    private void stubSearch(String term, UserRepresentation... users) {
        when(userResource.search(eq(term), isNull(), isNull(), eq(false))).thenReturn(Arrays.asList(users));
    }

    private static UserRepresentation user(String username) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        return user;
    }

    private static UserRepresentation user(String username, String webprotegeUsername) {
        UserRepresentation user = user(username);
        user.setAttributes(Map.of(KeycloakUserRepository.WEBPROTEGE_USERNAME_ATTRIBUTE,
                                  List.of(webprotegeUsername)));
        return user;
    }

    @Test
    public void GIVEN_anExistingUser_WHEN_queryForUsers_THEN_userIsCorrectlyMapped() {
        stubSearch("john", user("johndoe"));

        List<UserId> response = repository.findUserIdsFromName("john", false);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("johndoe", response.get(0).id());
    }

    @Test
    public void GIVEN_aLegacyUserWithWebprotegeUsernameAttribute_WHEN_queryForUsers_THEN_theAttributeIsReturnedAsTheUserId() {
        stubSearch("jdoe@x.org", user("jdoe@x.org", "jdoe"));

        List<UserId> response = repository.findUserIdsFromName("jdoe@x.org", true);

        assertEquals(1, response.size());
        assertEquals("jdoe", response.get(0).id());
    }

    @Test
    public void GIVEN_aUserWithoutTheAttribute_WHEN_queryForUsers_THEN_theKeycloakUsernameIsTheFallback() {
        stubSearch("service-account", user("service-account"));

        List<UserId> response = repository.findUserIdsFromName("service-account", false);

        assertEquals(1, response.size());
        assertEquals("service-account", response.get(0).id());
    }

    @Test
    public void GIVEN_anExactMatchQuery_WHEN_theSearchReturnsInfixHits_THEN_onlyTheExactUsernameSurvives() {
        stubSearch("anna", user("anna", "anna"), user("joanna", "joanna"));

        List<UserId> response = repository.findUserIdsFromName("anna", true);

        assertEquals(1, response.size());
        assertEquals("anna", response.get(0).id());
    }

    @Test
    public void GIVEN_missingUser_WHEN_queryForUsers_THEN_responseIsEmpty(){
        stubSearch("alice");

        List<UserId> response = repository.findUserIdsFromName("alice", false);

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void GIVEN_exception_WHEN_fetchForUsers_THEN_responseIsEmpty(){
        when(userResource.search(eq("bob"), isNull(), isNull(), eq(false))).thenThrow(new RuntimeException());

        List<UserId> response = repository.findUserIdsFromName("bob", false);

        assertNotNull(response);
        assertEquals(0, response.size());
    }
}
