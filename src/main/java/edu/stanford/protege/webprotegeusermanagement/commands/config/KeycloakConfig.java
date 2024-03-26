package edu.stanford.protege.webprotegeusermanagement.commands.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${webprotege.keycloak.serverUrl}")
    private String serverUrl;

    @Value("${webprotege.keycloak.realmName}")
    private String realmName;

    @Value("${webprotege.keycloak.username}")
    private String userName;

    @Value("${webprotege.keycloak.password}")
    private String password;

    @Value("${webprotege.keycloak.clientId}")
    private String clientId;

    @Value("${webprotege.keycloak.clientSecret}")
    private String clientSecret;

    @Bean
    public Keycloak getKeycloakAdminClient() {

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .grantType("client_credentials")
                .realm(realmName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(userName)
                .password(password)
                .build();
    }


}
