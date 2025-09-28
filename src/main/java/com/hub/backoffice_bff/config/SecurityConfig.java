package com.hub.backoffice_bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.*;
import java.util.stream.Collectors;


@Configuration
@EnableWebFluxSecurity
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

    @Bean
    @SuppressWarnings("unchecked")
    public GrantedAuthoritiesMapper adminAuthoritiesMapperForKeycloak() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            for (GrantedAuthority authority : authorities) {
                if (authority instanceof OidcUserAuthority oidcAuth) {
                    var oidcUserInfo = oidcAuth.getUserInfo();
                    if (oidcUserInfo.hasClaim(REALM_ACCESS_CLAIM)) {
                        var roles = (Collection<String>) ((Map<String, Object>) oidcUserInfo.getClaims()
                                .get(REALM_ACCESS_CLAIM))
                                .get(ROLES_CLAIM);
                        mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                    }
                } else if (authority instanceof OAuth2UserAuthority oauthAuth) {
                    Map<String, Object> userAttributes = oauthAuth.getAttributes();
                    if (userAttributes.containsKey(REALM_ACCESS_CLAIM)) {
                        var realmAccess = (Map<String, Object>) userAttributes.get(REALM_ACCESS_CLAIM);
                        var roles = (Collection<String>) realmAccess.get(ROLES_CLAIM);
                        mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                    }
                }
            }
            System.out.println("Roles from Keycloak: " + mappedAuthorities);
            return mappedAuthorities;
        };
    }

    private Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
        if (roles == null) return List.of();
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(PREFIX + role))
                .collect(Collectors.toList());
    }
}
