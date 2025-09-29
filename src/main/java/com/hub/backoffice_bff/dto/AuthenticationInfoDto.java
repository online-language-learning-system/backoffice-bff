package com.hub.backoffice_bff.dto;

public record AuthenticationInfoDto(
    boolean isAuthenticated, AuthenticatedUserDto authenticatedUser
){
}
