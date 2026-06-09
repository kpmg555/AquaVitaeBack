package com.aquavitae.interfaces.rest;

import com.aquavitae.application.usecase.CheckExternalApisUseCase;
import com.aquavitae.application.usecase.GetApiAlertsUseCase;
import com.aquavitae.application.usecase.GetApiKeysUseCase;
import com.aquavitae.application.usecase.GetApiStatusUseCase;
import com.aquavitae.application.usecase.RotateApiKeyUseCase;
import com.aquavitae.domain.models.ApiAlert;
import com.aquavitae.domain.models.ApiKeyInfo;
import com.aquavitae.domain.models.ApiMonitorStatus;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración para AdminApiMonitorResource.
 *
 * Este resource expone los endpoints utilizados por la pantalla administrativa
 * de monitoreo de APIs externas.
 *
 * Las pruebas levantan el contexto de Quarkus y realizan peticiones HTTP reales
 * mediante RestAssured. Los casos de uso se mockean con @InjectMock para evitar
 * llamadas reales a APIs externas, base de datos o rotación real de llaves.
 *
 * De esta forma se valida la integración entre la capa REST y los casos de uso
 * de la pantalla de APIs.
 */
@QuarkusTest
class AdminApiMonitorResourceTest {

    /**
     * Mock del caso de uso que obtiene el estado actual de las APIs externas.
     */
    @InjectMock
    GetApiStatusUseCase getApiStatusUseCase;

    /**
     * Mock del caso de uso que obtiene las alertas activas de APIs externas.
     */
    @InjectMock
    GetApiAlertsUseCase getApiAlertsUseCase;

    /**
     * Mock del caso de uso que obtiene información de llaves de APIs.
     */
    @InjectMock
    GetApiKeysUseCase getApiKeysUseCase;

    /**
     * Mock del caso de uso que rota una llave de API.
     */
    @InjectMock
    RotateApiKeyUseCase rotateApiKeyUseCase;

    /**
     * Mock del caso de uso que ejecuta la revisión manual de APIs externas.
     */
    @InjectMock
    CheckExternalApisUseCase checkExternalApisUseCase;

    /**
     * Verifica que el endpoint GET /admin/apis/status devuelva correctamente
     * el estado de las APIs externas.
     *
     * Escenario:
     * - El caso de uso devuelve una lista con el estado de Open-Meteo.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - La respuesta contiene los datos del estado de la API.
     * - Se verifica que el caso de uso fue ejecutado una vez.
     */
    @Test
    void getStatus_shouldReturn200WithApiStatusList() {
        when(getApiStatusUseCase.execute()).thenReturn(List.of(
                new ApiMonitorStatus(
                        "Open-Meteo",
                        "/v1/forecast",
                        "OK",
                        200,
                        "API funcionando correctamente",
                        0
                )
        ));

        given()
                .when()
                .get("/admin/apis/status")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].nombreApi", equalTo("Open-Meteo"))
                .body("[0].endpoint", equalTo("/v1/forecast"))
                .body("[0].estado", equalTo("OK"))
                .body("[0].ultimoCodigo", equalTo(200))
                .body("[0].mensaje", equalTo("API funcionando correctamente"))
                .body("[0].erroresActivos", equalTo(0));

        verify(getApiStatusUseCase, times(1)).execute();
    }

    /**
     * Verifica que el endpoint GET /admin/apis/status pueda devolver una lista vacía.
     *
     * Escenario:
     * - No hay estados registrados para las APIs externas.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - La respuesta es una lista vacía.
     * - Se verifica que el caso de uso fue ejecutado una vez.
     */
    @Test
    void getStatus_shouldReturn200WithEmptyListWhenThereAreNoStatuses() {
        when(getApiStatusUseCase.execute()).thenReturn(List.of());

        given()
                .when()
                .get("/admin/apis/status")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));

        verify(getApiStatusUseCase, times(1)).execute();
    }

    /**
     * Verifica que el endpoint GET /admin/apis/alerts devuelva correctamente
     * las alertas activas relacionadas con APIs externas.
     *
     * Escenario:
     * - El caso de uso devuelve una alerta activa de NASA POWER.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - La respuesta contiene los datos de la alerta.
     * - Se verifica que el caso de uso fue ejecutado una vez.
     */
    @Test
    void getAlerts_shouldReturn200WithApiAlertsList() {
        when(getApiAlertsUseCase.execute()).thenReturn(List.of(
                new ApiAlert(
                        "NASA POWER",
                        "/api/temporal/daily/point",
                        500,
                        "Error al consultar API externa",
                        "HIGH"
                )
        ));

        given()
                .when()
                .get("/admin/apis/alerts")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].nombreApi", equalTo("NASA POWER"))
                .body("[0].endpoint", equalTo("/api/temporal/daily/point"))
                .body("[0].codigoError", equalTo(500))
                .body("[0].mensaje", equalTo("Error al consultar API externa"))
                .body("[0].severidad", equalTo("HIGH"));

        verify(getApiAlertsUseCase, times(1)).execute();
    }

    /**
     * Verifica que el endpoint GET /admin/apis/alerts pueda devolver una lista vacía.
     *
     * Escenario:
     * - No hay alertas activas.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - La respuesta es una lista vacía.
     * - Se verifica que el caso de uso fue ejecutado una vez.
     */
    @Test
    void getAlerts_shouldReturn200WithEmptyListWhenThereAreNoAlerts() {
        when(getApiAlertsUseCase.execute()).thenReturn(List.of());

        given()
                .when()
                .get("/admin/apis/alerts")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));

        verify(getApiAlertsUseCase, times(1)).execute();
    }

    /**
     * Verifica que el endpoint GET /admin/apis/keys devuelva correctamente
     * la información de las llaves de APIs registradas en el sistema.
     *
     * Escenario:
     * - El caso de uso devuelve una lista con una llave de API activa.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - La respuesta contiene id, nombre, estado activo, expiración y fecha de rotación.
     * - Se verifica que el caso de uso fue ejecutado una vez.
     */
    @Test
    void getApiKeys_shouldReturn200WithApiKeysList() {
        when(getApiKeysUseCase.execute()).thenReturn(List.of(
                new ApiKeyInfo(
                        1,
                        "Open-Meteo API Key",
                        true,
                        "2026-12-31",
                        "2026-05-28"
                )
        ));

        given()
                .when()
                .get("/admin/apis/keys")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(1))
                .body("[0].nombre", equalTo("Open-Meteo API Key"))
                .body("[0].activa", equalTo(true))
                .body("[0].expiracion", equalTo("2026-12-31"))
                .body("[0].fechaRotacion", equalTo("2026-05-28"));

        verify(getApiKeysUseCase, times(1)).execute();
    }

    /**
     * Verifica que el endpoint GET /admin/apis/keys pueda devolver
     * una lista vacía cuando no hay llaves registradas.
     *
     * Escenario:
     * - El caso de uso no encuentra llaves de API.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - La respuesta es una lista vacía.
     * - Se verifica que el caso de uso fue ejecutado una vez.
     */
    @Test
    void getApiKeys_shouldReturn200WithEmptyListWhenThereAreNoKeys() {
        when(getApiKeysUseCase.execute()).thenReturn(List.of());

        given()
                .when()
                .get("/admin/apis/keys")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));

        verify(getApiKeysUseCase, times(1)).execute();
    }

    /**
     * Verifica que el endpoint POST /admin/apis/check ejecute la revisión
     * manual de APIs externas.
     *
     * Escenario:
     * - El administrador solicita verificar manualmente las APIs.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - Se ejecuta el caso de uso CheckExternalApisUseCase una vez.
     */
    @Test
    void checkApis_shouldReturn200AndExecuteUseCase() {
        doNothing().when(checkExternalApisUseCase).execute();

        given()
                .when()
                .post("/admin/apis/check")
                .then()
                .statusCode(200);

        verify(checkExternalApisUseCase, times(1)).execute();
    }

    /**
     * Verifica que el endpoint POST /admin/apis/keys/{id}/rotate
     * ejecute correctamente la rotación de una llave de API.
     *
     * Escenario:
     * - Se solicita rotar la llave con id 1.
     * - El caso de uso devuelve la información actualizada de la llave.
     *
     * Resultado esperado:
     * - El endpoint responde con código 200.
     * - La respuesta contiene la llave actualizada.
     * - Se verifica que el caso de uso fue ejecutado con el id correcto.
     */
    @Test
    void rotateApiKey_shouldReturn200WithRotatedApiKey() {
        Integer apiKeyId = 1;

        when(rotateApiKeyUseCase.execute(apiKeyId)).thenReturn(
                new ApiKeyInfo(
                        1,
                        "Open-Meteo API Key",
                        true,
                        "2026-12-31",
                        "2026-06-01"
                )
        );

        given()
                .when()
                .post("/admin/apis/keys/{id}/rotate", apiKeyId)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("nombre", equalTo("Open-Meteo API Key"))
                .body("activa", equalTo(true))
                .body("expiracion", equalTo("2026-12-31"))
                .body("fechaRotacion", equalTo("2026-06-01"));

        verify(rotateApiKeyUseCase, times(1)).execute(apiKeyId);
    }

    /**
     * Verifica el comportamiento del endpoint POST /admin/apis/keys/{id}/rotate
     * cuando el caso de uso no encuentra la llave solicitada.
     *
     * Escenario:
     * - Se solicita rotar una llave inexistente.
     * - El caso de uso lanza una RuntimeException.
     *
     * Resultado esperado:
     * - El endpoint responde con código 500, ya que el resource actual
     *   no captura explícitamente esta excepción.
     * - Se verifica que el caso de uso fue ejecutado con el id solicitado.
     */
    @Test
    void rotateApiKey_shouldReturn500WhenApiKeyDoesNotExist() {
        Integer apiKeyId = 999;

        when(rotateApiKeyUseCase.execute(apiKeyId))
                .thenThrow(new RuntimeException("API key not found: 999"));

        given()
                .when()
                .post("/admin/apis/keys/{id}/rotate", apiKeyId)
                .then()
                .statusCode(500);

        verify(rotateApiKeyUseCase, times(1)).execute(apiKeyId);
    }
}