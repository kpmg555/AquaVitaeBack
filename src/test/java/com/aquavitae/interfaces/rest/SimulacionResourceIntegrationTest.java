package com.aquavitae.interfaces.rest;

import com.aquavitae.infrastructure.entities.EstadoPlantaEntity;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

// SCRUM-233 · Pruebas de integración back — Dashboard Simulación
// Levanta Quarkus con H2 en memoria (application.properties de test) y prueba
// la cadena completa: REST -> caso de uso -> repositorio -> H2.
// Siembra una planta y un estado reales para que las consultas devuelvan datos.
@QuarkusTest
class SimulacionResourceIntegrationTest {

    @Inject
    EntityManager em;

    Integer plantaId;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpiar (hijo antes que padre por la FK)
        em.createQuery("DELETE FROM EstadoPlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM PlantaEntity").executeUpdate();

        // Planta con los costos/umbral que usan los KPIs
        PlantaEntity planta = new PlantaEntity();
        planta.setNombre("Planta Test");
        planta.setActiva(true);
        planta.setUmbralAlerta(0.75);
        planta.setCostoOperacionDiariaMxn(new BigDecimal("50000.00"));
        em.persist(planta);
        em.flush(); // para obtener el id generado
        plantaId = planta.getId();

        // Estado más reciente de esa planta (índice hídrico)
        EstadoPlantaEntity estado = new EstadoPlantaEntity();
        estado.setPlanta(planta);
        estado.setFechaRegistro(LocalDateTime.now());
        estado.setNivelAgua(72.0);
        estado.setIndiceHidrico(0.72);
        em.persist(estado);
    }

    @Test
    void getKpis_conPlantaSembrada_devuelveKpis() {
        given()
                .queryParam("plantaId", plantaId)
                .when().get("/api/simulacion/kpis")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("indiceHidricoActual", notNullValue())
                .body("diasHastaUmbralCritico", notNullValue())
                .body("probabilidadEventoCritico", notNullValue())
                .body("perdidaEconomicaProyectada", notNullValue());
    }

    @Test
    void getProyeccion_devuelvePuntosYBandas() {
        given()
                .queryParam("plantaId", plantaId)
                .queryParam("dias", 90)
                .when().get("/api/simulacion/proyeccion")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("puntos", not(empty()))
                .body("bandaSuperior", notNullValue())
                .body("bandaInferior", notNullValue())
                .body("puntos[0].dia", notNullValue())
                .body("puntos[0].valor", notNullValue());
    }

    @Test
    void getRecuperacion_devuelveAmbosEscenarios() {
        given()
                .queryParam("plantaId", plantaId)
                .queryParam("dias", 90)
                .when().get("/api/simulacion/recuperacion")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("conIntervencion", notNullValue())
                .body("sinIntervencion", notNullValue());
    }
}
