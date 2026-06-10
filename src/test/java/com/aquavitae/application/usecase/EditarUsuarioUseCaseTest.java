package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.EditarUsuarioDto;
import com.aquavitae.application.dto.UsuarioDto;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditarUsuarioUseCaseTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    FirebaseAuthPort firebaseAuthPort;

    @InjectMocks
    EditarUsuarioUseCase useCase;

    private EditarUsuarioDto dto;
    private Usuario existingUser;
    private final Integer userId = 10;

    @BeforeEach
    void setUp() {
        dto = new EditarUsuarioDto();
        dto.setNombre("Ana");
        dto.setApellido("Gómez");
        dto.setNombreUsuario("anagomez");
        dto.setCorreo("ana@example.com");
        dto.setTelefono("5556789");
        dto.setActivo(true);
        dto.setIdRol(3);
        dto.setNuevaContrasena("newPass456");
        dto.setAlcanceDatos("PLANTA_ONLY");
        dto.setIdPlantaAsignada(5);

        existingUser = new Usuario();
        existingUser.setId(userId);
        existingUser.setUuid("firebase-uuid-1");
        existingUser.setNombre("Juan");
        existingUser.setApellido("Pérez");
        existingUser.setCorreo("juan@example.com");
        existingUser.setActivo(true);
        existingUser.setIdRol(2);
    }

    @Test
    void execute_happyPath_updatesUserAndFirebase() {
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.existsByCorreo(dto.getCorreo())).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        UsuarioDto result = useCase.execute(userId, dto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Ana Gómez", result.getNombreCompleto());
        assertEquals("Ana", result.getNombre());
        assertEquals("Gómez", result.getApellido());
        assertEquals("anagomez", result.getNombreUsuario());
        assertEquals("5556789", result.getTelefono());
        assertEquals("ana@example.com", result.getCorreo());
        assertEquals(3, result.getIdRol());
        assertTrue(result.isActivo());

        verify(usuarioRepository).findById(userId);
        verify(usuarioRepository).existsByCorreo(dto.getCorreo());
        verify(usuarioRepository).save(existingUser);
        verify(firebaseAuthPort).actualizarUsuario("firebase-uuid-1", "ana@example.com", "Ana Gómez", "newPass456");

        assertEquals("Ana", existingUser.getNombre());
        assertEquals("Gómez", existingUser.getApellido());
        assertEquals("anagomez", existingUser.getNombreUsuario());
        assertEquals("PLANTA_ONLY", existingUser.getAlcanceDatos());
        assertEquals(5, existingUser.getIdPlantaAsignada());
    }

    @Test
    void execute_userNotFound_throwsNoSuchElementException() {
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class,
                () -> useCase.execute(userId, dto));
        verify(usuarioRepository, never()).save(any());
        verify(firebaseAuthPort, never()).actualizarUsuario(any(), any(), any(), any());
    }

    @Test
    void execute_emailChangedAndAlreadyExists_throwsIllegalArgumentException() {
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.existsByCorreo(dto.getCorreo())).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> useCase.execute(userId, dto));
        assertEquals("Correo ya registrado: ana@example.com", ex.getMessage());
    }

    @Test
    void execute_emailNotChanged_doesNotCheckExistence() {
        dto.setCorreo("juan@example.com"); // mismo correo que el existente
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(existingUser);

        useCase.execute(userId, dto);

        verify(usuarioRepository, never()).existsByCorreo(anyString());
        verify(firebaseAuthPort).actualizarUsuario(any(), any(), any(), any());
    }

    @Test
    void execute_withModulos_passesModulosToSavedUser() {
        List<String> modulos = Arrays.asList("Inventario", "Reportes");
        dto.setModulos(modulos);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(usuarioRepository.existsByCorreo(dto.getCorreo())).thenReturn(false);

        Usuario savedUser = new Usuario();
        savedUser.setId(userId);
        savedUser.setUuid("firebase-uuid-1");
        savedUser.setNombre(dto.getNombre());
        savedUser.setApellido(dto.getApellido());
        savedUser.setCorreo(dto.getCorreo());
        savedUser.setIdRol(dto.getIdRol());
        savedUser.setActivo(true);
        savedUser.setModulosPersonalizados(modulos);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        UsuarioDto result = useCase.execute(userId, dto);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals(modulos, captor.getValue().getModulosPersonalizados());

        assertEquals(modulos, result.getModulosEfectivos());
    }
}