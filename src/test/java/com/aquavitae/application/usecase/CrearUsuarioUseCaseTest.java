package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.CrearUsuarioDto;
import com.aquavitae.application.dto.UsuarioDto;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioUseCaseTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    FirebaseAuthPort firebaseAuthPort;

    @InjectMocks
    CrearUsuarioUseCase useCase;

    private CrearUsuarioDto dto;
    private final String firebaseUuid = "firebase-123";

    @BeforeEach
    void setUp() {
        dto = new CrearUsuarioDto();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setCorreo("juan@example.com");
        dto.setTelefono("5551234");
        dto.setIdRol(2);
        dto.setIdEmpresa(1);
        dto.setContrasenaTemp("TempPass123");
    }

    @Test
    void execute_happyPath_createsUserAndReturnsDto() {
        // Mock: correo no existe
        when(usuarioRepository.existsByCorreo(dto.getCorreo())).thenReturn(false);
        when(firebaseAuthPort.crearUsuario(dto.getCorreo(), dto.getContrasenaTemp(),
                dto.getNombre() + " " + dto.getApellido()))
                .thenReturn(firebaseUuid);

        // Mock: guardado en BD
        Usuario savedUsuario = new Usuario();
        savedUsuario.setId(100);
        savedUsuario.setUuid(firebaseUuid);
        savedUsuario.setNombre(dto.getNombre());
        savedUsuario.setApellido(dto.getApellido());
        savedUsuario.setCorreo(dto.getCorreo());
        savedUsuario.setTelefono(dto.getTelefono());
        savedUsuario.setIdRol(dto.getIdRol());
        savedUsuario.setIdEmpresa(dto.getIdEmpresa());
        savedUsuario.setActivo(true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUsuario);

        // Act
        UsuarioDto result = useCase.execute(dto);

        // Assert
        assertNotNull(result);
        assertEquals(100, result.getId());
        assertEquals("Juan Pérez", result.getNombreCompleto());
        assertEquals("juan@example.com", result.getCorreo());
        assertEquals(2, result.getIdRol());
        assertTrue(result.isActivo());

        // Verificar interacciones
        verify(usuarioRepository).existsByCorreo(dto.getCorreo());
        verify(firebaseAuthPort).crearUsuario(dto.getCorreo(), dto.getContrasenaTemp(), "Juan Pérez");
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario captured = usuarioCaptor.getValue();
        assertEquals(firebaseUuid, captured.getUuid());
        assertEquals(dto.getNombre(), captured.getNombre());
        assertEquals(dto.getApellido(), captured.getApellido());
        assertTrue(captured.isActivo());
    }

    @Test
    void execute_duplicateEmail_throwsIllegalArgumentException() {
        when(usuarioRepository.existsByCorreo(dto.getCorreo())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(dto));
        assertEquals("Correo ya registrado: juan@example.com", ex.getMessage());

        verify(usuarioRepository, never()).save(any());
        verify(firebaseAuthPort, never()).crearUsuario(any(), any(), any());
    }

    @Test
    void execute_firebaseThrowsException_throwsRuntimeException() {
        when(usuarioRepository.existsByCorreo(dto.getCorreo())).thenReturn(false);
        when(firebaseAuthPort.crearUsuario(any(), any(), any()))
                .thenThrow(new RuntimeException("Firebase error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.execute(dto));
        assertTrue(ex.getMessage().contains("Error en Firebase Auth"));

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void execute_repositorySaveThrowsException_disablesFirebaseUserAndThrows() {
        when(usuarioRepository.existsByCorreo(dto.getCorreo())).thenReturn(false);
        when(firebaseAuthPort.crearUsuario(any(), any(), any())).thenReturn(firebaseUuid);
        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> useCase.execute(dto));
        assertEquals("Error al guardar en BD", ex.getMessage());

        verify(firebaseAuthPort).deshabilitarUsuario(firebaseUuid);
    }
}