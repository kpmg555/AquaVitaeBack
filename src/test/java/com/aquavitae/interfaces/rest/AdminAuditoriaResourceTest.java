package com.aquavitae.interfaces.rest;

import com.aquavitae.application.usecase.GetAuditoriaLogDetailUseCase;
import com.aquavitae.application.usecase.GetAuditoriaLogsUseCase;
import com.aquavitae.application.usecase.GetAuditoriaResumenUseCase;
import com.aquavitae.domain.models.AuditoriaLog;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas de integración para AdminAuditoriaResource.
 *
 * Este resource expone los endpoints administrativos de auditoría:
 * - GET /admin/auditoria/resumen
 * - GET /admin/auditoria/logs
 * - GET /admin/auditoria/logs/{id}
 *
 * Las pruebas levantan Quarkus y usan RestAssured para realizar peticiones
 * HTTP reales. Los casos de uso se mockean con @InjectMock para evitar
 * dependencia directa de la base de datos.
 */
@QuarkusTest
class AdminAuditoriaResourceTest {

    @InjectMock
    GetAuditoriaLogsUseCase getAuditoriaLogsUseCase;

    @InjectMock
    GetAuditoriaLogDetailUseCase getAuditoriaLogDetailUseCase;

    @InjectMock
    GetAuditoriaResumenUseCase getAuditoriaResumenUseCase;

    /**
     * Verifica que GET /admin/auditoria/resumen devuelva los indicadores
     * principales de auditoría.
     */
    @Test
    void getResumen_shouldReturn200WithAuditoriaSummary() {
        when(getAuditoriaResumenUseCase.execute()).thenReturn(Map.of(
                "eventosHoy", 12,
                "cambiosCriticos", 3,
                "usuariosAuditados", 5,
                "registrosInmutables", 40
        ));

        given()
                .when()
                .get("/admin/auditoria/resumen")
                .then()
                .statusCode(200)
                .body("eventosHoy", equalTo(12))
                .body("cambiosCriticos", equalTo(3))
                .body("usuariosAuditados", equalTo(5))
                .body("registrosInmutables", equalTo(40));

        verify(getAuditoriaResumenUseCase, times(1)).execute();
    }

    /**
     * Verifica que GET /admin/auditoria/logs devuelva una lista de logs
     * usando filtros por query params.
     */
    @Test
    void getLogs_shouldReturn200WithFilteredLogs() {
        AuditoriaLog log = new AuditoriaLog(
                1,
                10,
                "Carlos",
                "CREATE",
                "USUARIOS",
                "Usuario",
                "Usuario creado correctamente",
                "127.0.0.1",
                "INFO",
                "{}",
                "{\"nombre\":\"Carlos\"}",
                LocalDateTime.of(2026, 6, 1, 10, 30)
        );

        when(getAuditoriaLogsUseCase.execute(
                10,
                "Carlos",
                "CREATE",
                "USUARIOS",
                "INFO"
        )).thenReturn(List.of(log));

        given()
                .queryParam("limit", 10)
                .queryParam("usuario", "Carlos")
                .queryParam("accion", "CREATE")
                .queryParam("modulo", "USUARIOS")
                .queryParam("severidad", "INFO")
                .when()
                .get("/admin/auditoria/logs")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(1))
                .body("[0].idUsuario", equalTo(10))
                .body("[0].usuario", equalTo("Carlos"))
                .body("[0].accion", equalTo("CREATE"))
                .body("[0].modulo", equalTo("USUARIOS"))
                .body("[0].entidad", equalTo("Usuario"))
                .body("[0].descripcion", equalTo("Usuario creado correctamente"))
                .body("[0].ip", equalTo("127.0.0.1"))
                .body("[0].severidad", equalTo("INFO"))
                .body("[0].valorAnterior", equalTo("{}"))
                .body("[0].valorNuevo", equalTo("{\"nombre\":\"Carlos\"}"))
                .body("[0].inmutable", equalTo(true))
                .body("[0].hashIntegridad", notNullValue());

        verify(getAuditoriaLogsUseCase, times(1)).execute(
                10,
                "Carlos",
                "CREATE",
                "USUARIOS",
                "INFO"
        );
    }

    /**
     * Verifica que GET /admin/auditoria/logs pueda devolver una lista vacía.
     */
    @Test
    void getLogs_shouldReturn200WithEmptyListWhenThereAreNoLogs() {
        when(getAuditoriaLogsUseCase.execute(
                null,
                null,
                null,
                null,
                null
        )).thenReturn(List.of());

        given()
                .when()
                .get("/admin/auditoria/logs")
                .then()
                .statusCode(200)
                .body("$", hasSize(0));

        verify(getAuditoriaLogsUseCase, times(1)).execute(
                null,
                null,
                null,
                null,
                null
        );
    }

    /**
     * Verifica que GET /admin/auditoria/logs/{id} devuelva el detalle
     * de un registro específico.
     */
    @Test
    void getLogDetail_shouldReturn200WithLogDetail() {
        Integer logId = 1;

        AuditoriaLog log = new AuditoriaLog(
                1,
                10,
                "Carlos",
                "UPDATE",
                "APIS",
                "ApiKey",
                "Rotación de API key",
                "127.0.0.1",
                "WARNING",
                "{\"activa\":true}",
                "{\"activa\":true}",
                LocalDateTime.of(2026, 6, 1, 12, 0)
        );

        when(getAuditoriaLogDetailUseCase.execute(logId)).thenReturn(log);

        given()
                .when()
                .get("/admin/auditoria/logs/{id}", logId)
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("idUsuario", equalTo(10))
                .body("usuario", equalTo("Carlos"))
                .body("accion", equalTo("UPDATE"))
                .body("modulo", equalTo("APIS"))
                .body("entidad", equalTo("ApiKey"))
                .body("descripcion", equalTo("Rotación de API key"))
                .body("ip", equalTo("127.0.0.1"))
                .body("severidad", equalTo("WARNING"))
                .body("valorAnterior", equalTo("{\"activa\":true}"))
                .body("valorNuevo", equalTo("{\"activa\":true}"))
                .body("inmutable", equalTo(true))
                .body("hashIntegridad", notNullValue());

        verify(getAuditoriaLogDetailUseCase, times(1)).execute(logId);
    }

    /**
     * Verifica el comportamiento cuando se solicita un log inexistente.
     *
     * El resource actual no captura explícitamente RuntimeException,
     * por lo que Quarkus responde con error 500.
     */
    @Test
    void getLogDetail_shouldReturn500WhenLogDoesNotExist() {
        Integer logId = 999;

        when(getAuditoriaLogDetailUseCase.execute(logId))
                .thenThrow(new RuntimeException("Audit log not found"));

        given()
                .when()
                .get("/admin/auditoria/logs/{id}", logId)
                .then()
                .statusCode(500);

        verify(getAuditoriaLogDetailUseCase, times(1)).execute(logId);
    }
}