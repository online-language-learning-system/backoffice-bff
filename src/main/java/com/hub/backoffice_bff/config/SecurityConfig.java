package com.hub.backoffice_bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

@Configuration
public class SecurityConfig {

    private static final String REALM_ACCESS_CLAIM = "realm_access";
    private static final String ROLES_CLAIM = "roles";
    private static final String PREFIX = "ROLE_";

    private final ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;

    public SecurityConfig(ReactiveClientRegistrationRepository reactiveClientRegistrationRepository) {
        this.reactiveClientRegistrationRepository = reactiveClientRegistrationRepository;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) throws Exception {
        return serverHttpSecurity
                .authorizeExchange(
                        authorizeExchangeSpec -> {
                            authorizeExchangeSpec.anyExchange().hasRole("admin");
                        }
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2Login(Customizer.withDefaults())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .logout(logoutSpec -> logoutSpec.logoutSuccessHandler(oidcLogoutSuccessHandler()))
                .build();
    }

    private ServerLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedServerLogoutSuccessHandler oidcClientInitiatedServerLogoutSuccessHandler
                = new OidcClientInitiatedServerLogoutSuccessHandler(this.reactiveClientRegistrationRepository);
        String postLogoutRedirectUri = "{baseUrl}"; // {baseUrl} = scheme (http/https) + host + port + contextPath
        oidcClientInitiatedServerLogoutSuccessHandler.setPostLogoutRedirectUri(postLogoutRedirectUri);
        return oidcClientInitiatedServerLogoutSuccessHandler;
    }

}
