package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.GenerarContrasenaResponseDto;
import com.aquavitae.domain.ports.FirebaseAuthPort;

@ApplicationScoped
public class GenerarContrasenaUseCase {

    @Inject
    FirebaseAuthPort firebaseAuthPort;

    public GenerarContrasenaResponseDto execute() {
        String contrasena = firebaseAuthPort.generarContrasenaAleatoria();
        return new GenerarContrasenaResponseDto(contrasena);
    }
}