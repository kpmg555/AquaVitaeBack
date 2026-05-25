package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.AuthenticatedUser;

import java.util.Optional;

public interface AuthenticatedUserRepositoryPort {
    Optional<AuthenticatedUser> findByUidOrEmail(String uid, String email);
}