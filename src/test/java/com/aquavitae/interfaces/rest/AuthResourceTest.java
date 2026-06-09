package com.aquavitae.interfaces.rest;

import com.aquavitae.application.usecase.VerifyFirebaseTokenUseCase;
import com.aquavitae.domain.models.AuthenticatedUser;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración para AuthResource.
 *
 * Estas pruebas validan el comportamiento del endpoint /auth/me levantando
 * el contexto de Quarkus y realizando peticiones HTTP reales mediante
 * RestAssured.
 *
 * El caso de uso VerifyFirebaseTokenUseCase se mockea para evitar llamadas
 * reales a Firebase durante las pruebas. De esta forma se valida la integración
 * entre la capa REST y la lógica de autenticación sin depender de servicios
 * externos.
 */
@QuarkusTest
class AuthResourceTest {

    /**
     * Mock del caso de uso encargado de verificar el token de Firebase.
     *
     * Se usa @InjectMock para reemplazar el bean real dentro del contexto
     * de Quarkus durante la ejecución de las pruebas.
     */
    @InjectMock
    VerifyFirebaseTokenUseCase verifyFirebaseTokenUseCase;

    /**
     * Verifica que el endpoint /auth/me responda correctamente cuando
     * recibe un token válido.
     *
     * Escenario:
     * - Se envía un header Authorization con formato Bearer.
     * - El caso de uso devuelve un usuario autenticado.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - La respuesta contiene uid, email, nombre, apellido, rol y permisos.
     * - Se verifica que el caso de uso fue ejecutado con el token correcto.
     */
    @Test
    void me_shouldReturn200WithAuthenticatedUserWhenTokenIsValid() {
        String token = "valid-firebase-token";

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                "firebase-uid-123",
                "admin@aquavitae.com",
                "Carlos",
                "Olivarez",
                "ADMIN",
                List.of("DASHBOARD", "APIS", "AUDITORIA")
        );

        when(verifyFirebaseTokenUseCase.execute(token)).thenReturn(authenticatedUser);

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("uid", equalTo("firebase-uid-123"))
                .body("email", equalTo("admin@aquavitae.com"))
                .body("nombre", equalTo("Carlos"))
                .body("apellido", equalTo("Olivarez"))
                .body("rol", equalTo("ADMIN"))
                .body("permisos", hasItems("DASHBOARD", "APIS", "AUDITORIA"));

        verify(verifyFirebaseTokenUseCase, times(1)).execute(token);
    }

    /**
     * Verifica que el endpoint /auth/me rechace peticiones sin header
     * Authorization.
     *
     * Escenario:
     * - La petición no incluye token.
     *
     * Resultado esperado:
     * - El endpoint responde con código 401.
     * - La respuesta contiene un mensaje de error.
     * - El caso de uso no se ejecuta porque no hay token que validar.
     */
    @Test
    void me_shouldReturn401WhenAuthorizationHeaderIsMissing() {
        given()
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401)
                .body("error", equalTo("Missing or invalid Authorization header"));

        verify(verifyFirebaseTokenUseCase, never()).execute(anyString());
    }

    /**
     * Verifica que el endpoint /auth/me rechace headers Authorization
     * con formato inválido.
     *
     * Escenario:
     * - La petición incluye Authorization, pero no inicia con "Bearer ".
     *
     * Resultado esperado:
     * - El endpoint responde con código 401.
     * - La respuesta contiene un mensaje de error.
     * - El caso de uso no se ejecuta porque el formato del header es inválido.
     */
    @Test
    void me_shouldReturn401WhenAuthorizationHeaderIsInvalid() {
        given()
                .header("Authorization", "InvalidTokenFormat")
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401)
                .body("error", equalTo("Missing or invalid Authorization header"));

        verify(verifyFirebaseTokenUseCase, never()).execute(anyString());
    }

    /**
     * Verifica que el endpoint /auth/me responda con 401 cuando el token
     * es rechazado por la lógica de autenticación.
     *
     * Escenario:
     * - La petición incluye un header Authorization con formato correcto.
     * - El caso de uso lanza una excepción porque el token es inválido
     *   o el usuario no está registrado/activo.
     *
     * Resultado esperado:
     * - El endpoint responde con código 401.
     * - La respuesta contiene el mensaje de error generado por el caso de uso.
     */
    @Test
    void me_shouldReturn401WhenTokenVerificationFails() {
        String token = "invalid-firebase-token";

        when(verifyFirebaseTokenUseCase.execute(token))
                .thenThrow(new RuntimeException("Invalid Firebase token"));

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401)
                .body("error", equalTo("Invalid Firebase token"));

        verify(verifyFirebaseTokenUseCase, times(1)).execute(token);
    }
}