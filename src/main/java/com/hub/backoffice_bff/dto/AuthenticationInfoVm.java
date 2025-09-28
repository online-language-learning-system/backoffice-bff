package com.hub.backoffice_bff.dto;

public record AuthenticationInfoVm (
    boolean isAuthenticated, AuthenticatedUserVm authenticatedUser
){
}
