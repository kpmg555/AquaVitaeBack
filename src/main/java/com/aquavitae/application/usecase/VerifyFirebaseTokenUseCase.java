package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.AuthenticatedUser;
import com.aquavitae.domain.ports.AuthenticatedUserRepositoryPort;
import com.aquavitae.domain.ports.TokenVerifierPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VerifyFirebaseTokenUseCase {

    @Inject
    TokenVerifierPort tokenVerifierPort;

    @Inject
    AuthenticatedUserRepositoryPort authenticatedUserRepositoryPort;

    public AuthenticatedUser execute(String token) {
        AuthenticatedUser firebaseUser = tokenVerifierPort.verify(token);

        return authenticatedUserRepositoryPort
                .findByUidOrEmail(firebaseUser.getUid(), firebaseUser.getEmail())
                .orElseThrow(() -> new RuntimeException("User not registered or inactive"));
    }
}