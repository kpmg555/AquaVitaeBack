package com.aquavitae.domain.ports;

import com.aquavitae.domain.model.AuthenticatedUser;

public interface TokenVerifierPort {
    AuthenticatedUser verify(String token);
}