package com.aquavitae.infrastructure.firebase;

import com.aquavitae.domain.model.AuthenticatedUser;
import com.aquavitae.domain.ports.TokenVerifierPort;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FirebaseTokenVerifierAdapter implements TokenVerifierPort {

    @Override
    public AuthenticatedUser verify(String token) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);

            return new AuthenticatedUser(
                    decodedToken.getUid(),
                    decodedToken.getEmail()
            );

        } catch (Exception e) {
            throw new RuntimeException("Invalid Firebase token", e);
        }
    }
}