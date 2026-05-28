package com.aquavitae.infrastructure.repository;

import com.aquavitae.domain.models.DashboardRiesgo;
import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.infrastructure.entities.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class DashboardRepositoryImplIntegrationTest {

    @Inject
    DashboardRepositoryImpl dashboardRepository;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setUp() {
        em.createQuery("DELETE FROM AlertaEntity").executeUpdate();
        em.createQuery("DELETE FROM EstadoPlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM PlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM UbicacionEntity").executeUpdate();
    }

    @Test
    @Transactional
    void testObtenerDashboard_conPlantasActivasYEstados() {
        UbicacionEntity ubicacion = new UbicacionEntity();
        ubicacion.setId(1);
        ubicacion.setNombre("Zona Norte");
        ubicacion.setLatitud(-33.45);
        ubicacion.setLongitud(-70.65);
        em.persist(ubicacion);

        PlantaEntity planta = new PlantaEntity();
        planta.setNombre("Planta Solar");
        planta.setActiva(true);
        planta.setUbicacion(ubicacion);
        em.persist(planta);
        Integer plantaId = planta.getId();

        EstadoPlantaEntity estado1 = new EstadoPlantaEntity();
        estado1.setPlanta(planta);
        estado1.setIndiceHidrico(0.85);
        estado1.setFechaRegistro(LocalDateTime.now().minusDays(2));
        estado1.setNivelAgua(85.0);
        estado1.setTipoDato("historico");
        em.persist(estado1);

        EstadoPlantaEntity estado2 = new EstadoPlantaEntity();
        estado2.setPlanta(planta);
        estado2.setIndiceHidrico(0.72);
        estado2.setFechaRegistro(LocalDateTime.now().minusDays(1));
        estado2.setNivelAgua(72.0);
        estado2.setTipoDato("historico");
        em.persist(estado2);

        DashboardRiesgo dashboard = dashboardRepository.obtenerDashboard();
        List<PlantaRiesgo> plantasRiesgo = dashboard.getPlantas();

        assertEquals(1, plantasRiesgo.size());
        PlantaRiesgo pr = plantasRiesgo.get(0);
        assertEquals(plantaId, pr.getId());
        assertEquals("Planta Solar", pr.getNombrePlanta());
        assertEquals(-33.45, pr.getLatitud());
        assertEquals(-70.65, pr.getLongitud());
        assertEquals(0.72, pr.getIndiceHidrico());
        assertEquals("ALTO", pr.getNivelRiesgo());
        assertEquals("Zona Norte", pr.getUbicacionNombre());
        assertEquals(1, dashboard.getResumen().getAlto());
    }

    @Test
    @Transactional
    void testObtenerDashboard_plantaSinEstado_usarIndicePorDefecto1_0() {
        UbicacionEntity ubicacion = new UbicacionEntity();
        ubicacion.setId(2);
        ubicacion.setNombre("Sin Estado");
        ubicacion.setLatitud(0.0);
        ubicacion.setLongitud(0.0);
        em.persist(ubicacion);

        PlantaEntity planta = new PlantaEntity();
        planta.setNombre("Planta Sin Registro");
        planta.setActiva(true);
        planta.setUbicacion(ubicacion);
        em.persist(planta);

        DashboardRiesgo dashboard = dashboardRepository.obtenerDashboard();
        List<PlantaRiesgo> plantas = dashboard.getPlantas();
        assertEquals(1, plantas.size());
        assertEquals(1.0, plantas.get(0).getIndiceHidrico());
        assertEquals("ALTO", plantas.get(0).getNivelRiesgo());
    }

    @Test
    @Transactional
    void testObtenerDashboard_soloPlantasActivas() {
        UbicacionEntity u1 = new UbicacionEntity();
        u1.setId(3);
        u1.setNombre("Activa");
        u1.setLatitud(1.0);
        u1.setLongitud(1.0);
        em.persist(u1);
        PlantaEntity activa = new PlantaEntity();
        activa.setNombre("Activa");
        activa.setActiva(true);
        activa.setUbicacion(u1);
        em.persist(activa);

        UbicacionEntity u2 = new UbicacionEntity();
        u2.setId(4);
        u2.setNombre("Inactiva");
        u2.setLatitud(2.0);
        u2.setLongitud(2.0);
        em.persist(u2);
        PlantaEntity inactiva = new PlantaEntity();
        inactiva.setNombre("Inactiva");
        inactiva.setActiva(false);
        inactiva.setUbicacion(u2);
        em.persist(inactiva);

        DashboardRiesgo dashboard = dashboardRepository.obtenerDashboard();
        List<PlantaRiesgo> plantas = dashboard.getPlantas();
        assertEquals(1, plantas.size());
        assertEquals("Activa", plantas.get(0).getNombrePlanta());
    }
}