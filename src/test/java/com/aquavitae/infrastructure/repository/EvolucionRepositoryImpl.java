package com.aquavitae.infrastructure.repository;

import com.aquavitae.domain.models.EvolucionHidrica;
import com.aquavitae.infrastructure.entities.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EvolucionRepositoryImplIntegrationTest {

    @Inject
    EvolucionRepositoryImpl evolucionRepository;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setUp() {
        // Limpiar tablas en orden inverso a las FK
        em.createQuery("DELETE FROM AlertaEntity").executeUpdate();
        em.createQuery("DELETE FROM EstadoPlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM PlantaEntity").executeUpdate();
        em.createQuery("DELETE FROM UbicacionEntity").executeUpdate();
    }

    @Test
    @Transactional
    void testObtenerEvolucion_devuelvePuntosDiarios() {
        // Ubicación con ID manual (sin @GeneratedValue)
        UbicacionEntity ubicacion = new UbicacionEntity();
        ubicacion.setId(1);
        ubicacion.setNombre("UbicTest");
        ubicacion.setLatitud(0.0);
        ubicacion.setLongitud(0.0);
        em.persist(ubicacion);

        // Planta con ID generado automáticamente
        PlantaEntity planta = new PlantaEntity();
        planta.setNombre("PlantaTest");
        planta.setActiva(true);
        planta.setUbicacion(ubicacion);
        em.persist(planta);

        LocalDate hoy = LocalDate.now();

        // Estado 1: día actual
        EstadoPlantaEntity e1 = new EstadoPlantaEntity();
        e1.setPlanta(planta);
        e1.setIndiceHidrico(0.5);
        e1.setFechaRegistro(hoy.atStartOfDay());
        e1.setNivelAgua(50.0);
        em.persist(e1);

        // Estado 2: día siguiente
        EstadoPlantaEntity e2 = new EstadoPlantaEntity();
        e2.setPlanta(planta);
        e2.setIndiceHidrico(0.6);
        e2.setFechaRegistro(hoy.plusDays(1).atStartOfDay());
        e2.setNivelAgua(60.0);
        em.persist(e2);

        // Estado 3: otro registro el mismo día que e1 (para promediar)
        EstadoPlantaEntity e3 = new EstadoPlantaEntity();
        e3.setPlanta(planta);
        e3.setIndiceHidrico(0.7);
        e3.setFechaRegistro(hoy.atStartOfDay().plusHours(6));
        e3.setNivelAgua(70.0);
        em.persist(e3);

        EvolucionHidrica evolucion = evolucionRepository.obtenerEvolucion(10);
        List<EvolucionHidrica.PuntoDiario> puntos = evolucion.getPuntos();

        assertEquals(2, puntos.size());

        // Verificar primer punto (promedio del día actual: (0.5+0.7)/2 = 0.6)
        assertEquals(hoy, puntos.get(0).getFecha());
        // Usar delta para evitar errores de precisión en double
        assertEquals(0.6, puntos.get(0).getValorPromedio(), 0.0001);

        // Verificar segundo punto (día siguiente, valor 0.6)
        assertEquals(hoy.plusDays(1), puntos.get(1).getFecha());
        assertEquals(0.6, puntos.get(1).getValorPromedio(), 0.0001);
    }

    @Test
    @Transactional
    void testObtenerEvolucion_sinDatos_retornaListaVacia() {
        EvolucionHidrica evolucion = evolucionRepository.obtenerEvolucion(30);
        assertTrue(evolucion.getPuntos().isEmpty());
    }
}