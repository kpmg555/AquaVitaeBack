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

// SCRUM-237 · Pruebas de integración back — Dashboard Alternativas
// Levanta Quarkus con H2 y prueba la cadena REST -> caso de uso -> repositorio -> BD.
// Siembra una planta principal y una alterna (para que /ubicaciones devuelva resultados).
@QuarkusTest
class AlternativasResourceIntegrationTest {

    @Inject
    EntityManager em;

    Integer plantaPrincipalId;

    @BeforeEach
    @Transactional
    void setUp() {
        em.createQuery("DELETE FROM EstadoPlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM PlantaEntity").executeUpdate();

        PlantaEntity principal = crearPlantaConEstado("Planta Principal", 0.80, new BigDecimal("100000.00"));
        plantaPrincipalId = principal.getId();

        // Otra planta activa con estado: será la alternativa que devuelva /ubicaciones
        crearPlantaConEstado("Planta Alterna", 0.40, new BigDecimal("80000.00"));
    }

    private PlantaEntity crearPlantaConEstado(String nombre, double indiceHidrico, BigDecimal costoApertura) {
        PlantaEntity p = new PlantaEntity();
        p.setNombre(nombre);
        p.setActiva(true);
        p.setUmbralAlerta(0.75);
        p.setCostoCierreMxn(new BigDecimal("50000.00"));
        p.setCostoAperturaMxn(costoApertura);
        p.setCostoOperacionDiariaMxn(new BigDecimal("30000.00"));
        p.setDiasReaperturaMin(30);
        p.setDiasReaperturaMax(60);
        em.persist(p);
        em.flush();

        EstadoPlantaEntity e = new EstadoPlantaEntity();
        e.setPlanta(p);
        e.setFechaRegistro(LocalDateTime.now());
        e.setNivelAgua(70.0);
        e.setIndiceHidrico(indiceHidrico);
        em.persist(e);
        return p;
    }

    @Test
    void getAlerta_conPlanta_devuelveAlertaOperativa() {
        given()
                .queryParam("plantaId", plantaPrincipalId)
                .when().get("/api/alternativas/alerta")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("nombrePlanta", is("Planta Principal"))
                .body("indiceActual", notNullValue())
                .body("costoApertura", notNullValue())
                .body("diasReaperturaMin", notNullValue());
    }

    @Test
    void getAlerta_sinPlantaId_devuelve400() {
        given()
                .when().get("/api/alternativas/alerta")
                .then()
                .statusCode(400);
    }

    @Test
    void getUbicaciones_devuelveAlternativas() {
        given()
                .queryParam("plantaId", plantaPrincipalId)
                .when().get("/api/alternativas/ubicaciones")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].nombre", is("Planta Alterna"))
                .body("[0].costoTotal", notNullValue());
    }

    @Test
    void getFactores_devuelveFactores() {
        given()
                .queryParam("plantaId", plantaPrincipalId)
                .when().get("/api/alternativas/factores")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].nombre", notNullValue())
                .body("[0].puntos", notNullValue());
    }
}
