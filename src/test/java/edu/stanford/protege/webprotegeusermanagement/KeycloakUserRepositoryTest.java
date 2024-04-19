package edu.stanford.protege.webprotegeusermanagement;


import edu.stanford.protege.webprotege.common.UserId;
import edu.stanford.protege.webprotegeusermanagement.commands.dto.KeycloakUserRepository;
import org.apache.http.ConnectionClosedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
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

    @Test
    public void GIVEN_anExistingUser_WHEN_queryForUsers_THEN_userIsCorrectlyMapped() {
        UserRepresentation user = new UserRepresentation();
        user.setUsername("johndoe");
        when(userResource.search(eq("john"), eq(false))).thenReturn(Arrays.asList(user));

        List<UserId> response = repository.findUserIdsFromName("john");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("johndoe", response.get(0).id());
    }
    
    @Test
    public void GIVEN_missingUser_WHEN_queryForUsers_THEN_responseIsEmpty(){
        when(userResource.search(eq("alice"), eq(false))).thenReturn(new ArrayList<>());

        List<UserId> response = repository.findUserIdsFromName("alice");

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    public void GIVEN_exception_WHEN_fetchForUsers_THEN_responseIsEmpty(){
        when(userResource.search(eq("bob"), eq(false))).thenThrow(new RuntimeException());

        List<UserId> response = repository.findUserIdsFromName("bob");

        assertNotNull(response);
        assertEquals(0, response.size());
    }
}
