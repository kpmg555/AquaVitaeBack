package com.aquavitae.infrastructure.repository;

import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.infrastructure.entities.AlertaEntity;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import com.aquavitae.infrastructure.entities.UbicacionEntity;
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
class AlertaRepositoryImplIntegrationTest {

    @Inject
    AlertaRepositoryImpl alertaRepository;

    @Inject
    EntityManager em;

    private Integer plantaId; // Guardará el ID generado de la planta

    @BeforeEach
    @Transactional
    void setUp() {
        em.createQuery("DELETE FROM AlertaEntity").executeUpdate();
        em.createQuery("DELETE FROM EstadoPlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM PlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM UbicacionEntity").executeUpdate();

        // UbicacionEntity (ID manual)
        UbicacionEntity ubicacion = new UbicacionEntity();
        ubicacion.setId(1);
        ubicacion.setNombre("Ubicación Test");
        ubicacion.setLatitud(0.0);
        ubicacion.setLongitud(0.0);
        em.persist(ubicacion);

        // PlantaEntity (ID generado automáticamente)
        PlantaEntity planta = new PlantaEntity();
        planta.setNombre("Planta Test");
        planta.setActiva(true);
        planta.setUbicacion(ubicacion);
        em.persist(planta);
        plantaId = planta.getId(); // Guardamos el ID generado
    }

    @Test
    @Transactional
    void testSaveAndFindRecientes() {
        AlertaDominio alerta = new AlertaDominio(null, plantaId, "RIESGO", "Alerta test",
                "Desc", 0.2, 0.5, LocalDateTime.now());
        alertaRepository.save(alerta);
        List<AlertaDominio> results = alertaRepository.findRecientes(10);
        assertEquals(1, results.size());
    }

    @Test
    @Transactional
    void testFindRecientesRespectsLimit() {
        for (int i = 1; i <= 5; i++) {
            AlertaEntity entity = new AlertaEntity();
            entity.setIdPlanta(plantaId);
            entity.setTipo("TIPO");
            entity.setTitulo("Alerta " + i);
            entity.setDescripcion("Desc");
            entity.setNivelActual(0.1);
            entity.setUmbral(0.5);
            entity.setFecha(LocalDateTime.now().minusDays(i));
            em.persist(entity);
        }
        List<AlertaDominio> results = alertaRepository.findRecientes(3);
        assertEquals(3, results.size());
    }

    @Test
    @Transactional
    void testSaveWithNullFecha_assignsCurrentTimestamp() {
        AlertaDominio alerta = new AlertaDominio(null, plantaId, "INFO", "Sin fecha",
                "Test", 0.3, 0.6, null);
        alertaRepository.save(alerta);
        List<AlertaDominio> saved = alertaRepository.findRecientes(1);
        assertNotNull(saved.get(0).getFecha());
    }
}