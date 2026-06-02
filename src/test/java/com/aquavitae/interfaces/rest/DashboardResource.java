package com.aquavitae.interfaces.rest;

import com.aquavitae.infrastructure.entities.*;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class DashboardResourceIntegrationTest {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setUpData() {
        em.createQuery("DELETE FROM AlertaEntity").executeUpdate();
        em.createQuery("DELETE FROM EstadoPlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM PlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM UbicacionEntity").executeUpdate();

        UbicacionEntity ubicacion = new UbicacionEntity();
        ubicacion.setId(1);
        ubicacion.setNombre("Ciudad Test");
        ubicacion.setLatitud(-33.45);
        ubicacion.setLongitud(-70.65);
        em.persist(ubicacion);

        PlantaEntity planta = new PlantaEntity();
        planta.setNombre("Planta Prueba");
        planta.setActiva(true);
        planta.setUbicacion(ubicacion);
        em.persist(planta);
        Integer plantaId = planta.getId();

        EstadoPlantaEntity estado = new EstadoPlantaEntity();
        estado.setPlanta(planta);
        estado.setIndiceHidrico(0.82);
        estado.setFechaRegistro(LocalDateTime.now());
        estado.setNivelAgua(82.0);
        em.persist(estado);

        AlertaEntity alerta1 = new AlertaEntity();
        alerta1.setIdPlanta(plantaId);
        alerta1.setTipo("RIESGO");
        alerta1.setTitulo("Alerta 1");
        alerta1.setDescripcion("Desc1");
        alerta1.setNivelActual(0.3);
        alerta1.setUmbral(0.5);
        alerta1.setFecha(LocalDateTime.now().minusHours(1));
        em.persist(alerta1);

        AlertaEntity alerta2 = new AlertaEntity();
        alerta2.setIdPlanta(plantaId);
        alerta2.setTipo("INFO");
        alerta2.setTitulo("Alerta 2");
        alerta2.setDescripcion("Desc2");
        alerta2.setNivelActual(0.2);
        alerta2.setUmbral(0.5);
        alerta2.setFecha(LocalDateTime.now().minusHours(2));
        em.persist(alerta2);
    }

    @Test
    void testGetDashboard() {
        given()
                .when().get("/api/dashboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("plantas.size()", is(1))
                .body("plantas[0].idPlanta", notNullValue())
                .body("plantas[0].nombrePlanta", is("Planta Prueba"))
                .body("plantas[0].indiceHidrico", is(0.82f))
                .body("plantas[0].nivelRiesgo", is("ALTO"))
                .body("resumen.alto", is(1));
    }

    @Test
    void testGetAlertas_defaultLimit() {
        given()
                .when().get("/api/alertas")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].titulo", is("Alerta 1"))
                .body("[1].titulo", is("Alerta 2"));
    }

    @Test
    void testGetAlertas_withCustomLimit() {
        given()
                .queryParam("limit", 1)
                .when().get("/api/alertas")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].titulo", is("Alerta 1"));
    }

    @Test
    void testGetEvolucion_default30Days() {
        given()
                .when().get("/api/evolucion")
                .then()
                .statusCode(200)
                .body("puntos.size()", is(1))
                .body("puntos[0].valorPromedio", is(0.82f));
    }

    @Test
    void testGetEvolucion_withDiasParameter() {
        given()
                .queryParam("dias", 5)
                .when().get("/api/evolucion")
                .then()
                .statusCode(200)
                .body("puntos.size()", greaterThanOrEqualTo(0));
    }
}