package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EliminarUsuarioUseCaseTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    FirebaseAuthPort firebaseAuthPort;

    @InjectMocks
    EliminarUsuarioUseCase useCase;

    private Usuario usuarioActivo;
    private final Integer userId = 1;

    @BeforeEach
    void setUp() {
        usuarioActivo = new Usuario();
        usuarioActivo.setId(userId);
        usuarioActivo.setUuid("fire-uid");
        usuarioActivo.setActivo(true);
    }

    @Test
    void execute_happyPath_disablesUserInFirebaseAndDB() {
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioActivo));

        useCase.execute(userId);

        verify(firebaseAuthPort).deshabilitarUsuario("fire-uid");
        verify(usuarioRepository).desactivar(userId);
    }

    @Test
    void execute_userNotFound_throwsNoSuchElementException() {
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class,
                () -> useCase.execute(userId));
        verify(firebaseAuthPort, never()).deshabilitarUsuario(any());
        verify(usuarioRepository, never()).desactivar(any());
    }

    @Test
    void execute_userAlreadyInactive_throwsIllegalStateException() {
        usuarioActivo.setActivo(false);
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioActivo));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> useCase.execute(userId));
        assertEquals("El usuario ya está desactivado", ex.getMessage());
    }

    @Test
    void execute_firebaseFails_continuesAndDisablesInDB() {
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioActivo));
        doThrow(new RuntimeException("Firebase error")).when(firebaseAuthPort).deshabilitarUsuario("fire-uid");

        // No debe lanzar excepción (solo loguea error)
        useCase.execute(userId);

        verify(firebaseAuthPort).deshabilitarUsuario("fire-uid");
        verify(usuarioRepository).desactivar(userId);
    }
}