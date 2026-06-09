package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.AuthenticatedUser;
import com.aquavitae.domain.ports.AuthenticatedUserRepositoryPort;
import com.aquavitae.domain.ports.TokenVerifierPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para VerifyFirebaseTokenUseCase.
 *
 * Este caso de uso representa la validación del login/autenticación en backend.
 * Su responsabilidad es recibir un token de Firebase, verificarlo mediante
 * TokenVerifierPort y posteriormente buscar al usuario autenticado dentro
 * del sistema mediante AuthenticatedUserRepositoryPort.
 *
 * Las pruebas se realizan con Mockito para evitar dependencias reales con
 * Firebase o base de datos. De esta forma se prueba únicamente la lógica
 * del caso de uso.
 */
class VerifyFirebaseTokenUseCaseTest {

    /**
     * Puerto encargado de verificar el token de Firebase.
     * Se mockea para evitar llamadas reales a Firebase durante la prueba.
     */
    private TokenVerifierPort tokenVerifierPort;

    /**
     * Puerto encargado de consultar al usuario autenticado en el sistema.
     * Se mockea para evitar depender de una base de datos real.
     */
    private AuthenticatedUserRepositoryPort authenticatedUserRepositoryPort;

    /**
     * Caso de uso que será probado.
     */
    private VerifyFirebaseTokenUseCase verifyFirebaseTokenUseCase;

    /**
     * Configuración inicial antes de cada prueba.
     *
     * Se crean los mocks necesarios y se inyectan manualmente en el caso de uso,
     * ya que esta prueba es unitaria y no levanta el contexto completo de Quarkus.
     */
    @BeforeEach
    void setUp() {
        tokenVerifierPort = mock(TokenVerifierPort.class);
        authenticatedUserRepositoryPort = mock(AuthenticatedUserRepositoryPort.class);

        verifyFirebaseTokenUseCase = new VerifyFirebaseTokenUseCase();
        verifyFirebaseTokenUseCase.tokenVerifierPort = tokenVerifierPort;
        verifyFirebaseTokenUseCase.authenticatedUserRepositoryPort = authenticatedUserRepositoryPort;
    }

    /**
     * Verifica el flujo exitoso de autenticación.
     *
     * Escenario:
     * - El token de Firebase es válido.
     * - Firebase devuelve un usuario con UID y correo.
     * - El usuario existe y está activo dentro del sistema.
     *
     * Resultado esperado:
     * - El caso de uso devuelve el usuario autenticado con sus datos completos.
     * - Se verifica que el token fue validado una sola vez.
     * - Se verifica que el repositorio fue consultado con el UID y correo correctos.
     */
    @Test
    void execute_shouldReturnAuthenticatedUserWhenTokenIsValidAndUserExists() {
        String token = "valid-firebase-token";

        AuthenticatedUser firebaseUser = new AuthenticatedUser(
                "firebase-uid-123",
                "admin@aquavitae.com"
        );

        AuthenticatedUser registeredUser = new AuthenticatedUser(
                "firebase-uid-123",
                "admin@aquavitae.com",
                "Carlos",
                "Olivarez",
                "ADMIN",
                List.of("DASHBOARD", "APIS", "AUDITORIA")
        );

        when(tokenVerifierPort.verify(token)).thenReturn(firebaseUser);
        when(authenticatedUserRepositoryPort.findByUidOrEmail(
                "firebase-uid-123",
                "admin@aquavitae.com"
        )).thenReturn(Optional.of(registeredUser));

        AuthenticatedUser result = verifyFirebaseTokenUseCase.execute(token);

        assertNotNull(result);
        assertEquals("firebase-uid-123", result.getUid());
        assertEquals("admin@aquavitae.com", result.getEmail());
        assertEquals("Carlos", result.getNombre());
        assertEquals("Olivarez", result.getApellido());
        assertEquals("ADMIN", result.getRol());
        assertEquals(List.of("DASHBOARD", "APIS", "AUDITORIA"), result.getPermisos());

        verify(tokenVerifierPort, times(1)).verify(token);
        verify(authenticatedUserRepositoryPort, times(1))
                .findByUidOrEmail("firebase-uid-123", "admin@aquavitae.com");
    }

    /**
     * Verifica el comportamiento cuando el token es válido,
     * pero el usuario no existe o está inactivo dentro del sistema.
     *
     * Escenario:
     * - Firebase valida correctamente el token.
     * - El repositorio no encuentra un usuario activo con ese UID o correo.
     *
     * Resultado esperado:
     * - El caso de uso lanza una RuntimeException.
     * - El mensaje de error indica que el usuario no está registrado o está inactivo.
     * - Se verifica que sí se consultó el repositorio.
     */
    @Test
    void execute_shouldThrowRuntimeExceptionWhenUserIsNotRegisteredOrInactive() {
        String token = "valid-token-but-user-not-found";

        AuthenticatedUser firebaseUser = new AuthenticatedUser(
                "firebase-uid-456",
                "missing@aquavitae.com"
        );

        when(tokenVerifierPort.verify(token)).thenReturn(firebaseUser);
        when(authenticatedUserRepositoryPort.findByUidOrEmail(
                "firebase-uid-456",
                "missing@aquavitae.com"
        )).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            verifyFirebaseTokenUseCase.execute(token);
        });

        assertEquals("User not registered or inactive", exception.getMessage());

        verify(tokenVerifierPort, times(1)).verify(token);
        verify(authenticatedUserRepositoryPort, times(1))
                .findByUidOrEmail("firebase-uid-456", "missing@aquavitae.com");
    }

    /**
     * Verifica el comportamiento cuando la validación del token falla.
     *
     * Escenario:
     * - El token recibido es inválido.
     * - TokenVerifierPort lanza una excepción al intentar validarlo.
     *
     * Resultado esperado:
     * - El caso de uso propaga la excepción correspondiente.
     * - No se consulta el repositorio de usuarios, ya que no existe un usuario
     *   Firebase válido del cual obtener UID o correo.
     */
    @Test
    void execute_shouldThrowRuntimeExceptionWhenTokenVerifierFails() {
        String token = "invalid-firebase-token";

        when(tokenVerifierPort.verify(token))
                .thenThrow(new RuntimeException("Invalid Firebase token"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            verifyFirebaseTokenUseCase.execute(token);
        });

        assertEquals("Invalid Firebase token", exception.getMessage());

        verify(tokenVerifierPort, times(1)).verify(token);
        verify(authenticatedUserRepositoryPort, never()).findByUidOrEmail(any(), any());
    }
}