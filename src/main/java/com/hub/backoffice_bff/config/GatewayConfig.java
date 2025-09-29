package com.hub.backoffice_bff.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final ServiceUrlConfig serviceUrlConfig;

    public GatewayConfig(ServiceUrlConfig serviceUrlConfig) {
        this.serviceUrlConfig = serviceUrlConfig;
    }

    @Bean
    public RouteLocator gatewayFrontend(RouteLocatorBuilder builder) {

        String backofficeUri = serviceUrlConfig.services().get("backoffice");

        return builder.routes()
                .route("backoffice", r -> r.path("/**")
                        .filters(f -> f.removeRequestHeader("Host"))
                        .uri(backofficeUri))
                .build();
    }

}
