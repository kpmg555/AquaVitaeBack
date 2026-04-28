package com.aquavitae.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import com.aquavitae.infrastructure.mapper.PlantaRiesgoMapper;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped

public class PlantaRepositoryImpl implements PlantaRepository,
        PanacheRepositoryBase<PlantaEntity, Integer > {

    @Inject EntityManager em;

    @Override
    public List<PlantaRiesgo> findAllActivasConRiesgo() {

        // Query plantas activas con su ubicación
        List<PlantaEntity> plantas = em.createQuery(
                "SELECT p FROM PlantaEntity p " +
                        "LEFT JOIN FETCH p.ubicacion " +
                        "WHERE p.activa = true",
                PlantaEntity.class
        ).getResultList();

        List<PlantaRiesgo> resultado = new ArrayList<>();

        for (PlantaEntity planta : plantas) {

            // Query último índice hídrico (tiempo real)
            Float indiceActual = em.createQuery(
                            "SELECT e.indiceHidrico FROM EstadoPlantaEntity e " +
                                    "WHERE e.idPlanta = :id " +
                                    "  AND e.tipoDato IN ('pronostico_openmeteo','pronostico_smn') " +
                                    "ORDER BY e.fechaRegistro DESC",
                            Float.class)
                    .setParameter("id", planta.getId())
                    .setMaxResults(1)
                    .getResultStream().findFirst().orElse(null);

            // Query C: índice de hace 7 días (para tendencia)
            Float indiceHace7Dias = em.createQuery(
                            "SELECT e.indiceHidrico FROM EstadoPlantaEntity e " +
                                    "WHERE e.idPlanta = :id " +
                                    "  AND e.tipoDato IN ('pronostico_openmeteo','pronostico_smn') " +
                                    "  AND e.fechaRegistro <= :hace7dias " +
                                    "ORDER BY e.fechaRegistro DESC",
                            Float.class)
                    .setParameter("id", planta.getId())
                    .setParameter("hace7dias",
                            java.sql.Timestamp.valueOf(
                                    java.time.LocalDate.now().minusDays(7).atTime(23, 59)))
                    .setMaxResults(1)
                    .getResultStream().findFirst().orElse(null);

            // Query ¿tiene alerta crítica activa?
            Long alertas = em.createQuery(
                            "SELECT COUNT(a) FROM AlertaEntity a " +
                                    "WHERE a.idPlanta = :id AND a.tipo = 'CRÍTICO'",
                            Long.class)
                    .setParameter("id", planta.getId())
                    .getSingleResult();

            resultado.add(PlantaRiesgoMapper.toDomain(
                    planta, indiceActual, indiceHace7Dias, alertas > 0));
        }
        return resultado;
    }
}
