package com.hub.backoffice_bff.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(value = "service-uri")
public record ServiceUrlConfig(
        Map<String, String> services,
        String tokenId,
        String tokenSecret
) {
}
