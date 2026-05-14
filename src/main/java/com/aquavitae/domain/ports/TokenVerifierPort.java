package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.AuthenticatedUser;

public interface TokenVerifierPort {
    AuthenticatedUser verify(String token);
}