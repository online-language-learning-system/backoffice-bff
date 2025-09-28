package com.hub.backoffice_bff.controller;

import com.hub.backoffice_bff.dto.AuthenticatedUserVm;
import com.hub.backoffice_bff.dto.AuthenticationInfoVm;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @GetMapping("/authentication")
    public ResponseEntity<AuthenticationInfoVm> user(@AuthenticationPrincipal OAuth2User principal) {

        if (principal == null)
            return ResponseEntity.ok(new AuthenticationInfoVm(false, null));

        AuthenticatedUserVm authenticatedUserVm =
                new AuthenticatedUserVm(principal.getAttribute("preferred_username"));
        return ResponseEntity.ok(new AuthenticationInfoVm(true, authenticatedUserVm));
    }
}
