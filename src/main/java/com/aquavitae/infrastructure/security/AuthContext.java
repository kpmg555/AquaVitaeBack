package com.aquavitae.infrastructure.security;

import com.aquavitae.application.usecase.VerifyFirebaseTokenUseCase;
import com.aquavitae.domain.models.AuthenticatedUser;
import com.aquavitae.domain.repository.UsuarioAuthRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;

@RequestScoped
public class AuthContext {

    @Inject
    VerifyFirebaseTokenUseCase verifyFirebaseTokenUseCase;
    @Inject
    HttpHeaders httpHeaders;
    @Inject
    UsuarioAuthRepository usuarioAuthRepository;

    private AuthenticatedUser _user;

    public AuthenticatedUser getUser() {
        if (_user != null)
            return _user;

        String header = httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            throw new jakarta.ws.rs.NotAuthorizedException("Bearer token requerido");
        }
        String token = header.substring("Bearer ".length()).trim();

        AuthenticatedUser baseUser;
        try {
            baseUser = verifyFirebaseTokenUseCase.execute(token);
        } catch (Exception e) {
            throw new jakarta.ws.rs.NotAuthorizedException("Token de Firebase inválido");
        }

        _user = usuarioAuthRepository.findByUid(baseUser.getUid())
                .map(data -> new AuthenticatedUser(
                        baseUser.getUid(),
                        baseUser.getEmail(),
                        data.getIdEmpresa(),
                        data.getIdRol(),
                        data.getNombre() + " " + data.getApellido()))
                .orElseThrow(() -> new jakarta.ws.rs.NotAuthorizedException("Usuario no registrado en el sistema"));

        return _user;
    }
}