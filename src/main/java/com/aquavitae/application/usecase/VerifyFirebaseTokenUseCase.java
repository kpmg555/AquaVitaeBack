package com.aquavitae.application.usecase;

import com.aquavitae.domain.model.AuthenticatedUser;
import com.aquavitae.domain.ports.TokenVerifierPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VerifyFirebaseTokenUseCase {

    @Inject
    TokenVerifierPort tokenVerifierPort;

    public AuthenticatedUser execute(String token) {
        return tokenVerifierPort.verify(token);
    }
}