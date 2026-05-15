package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.domain.models.DashboardRiesgo;
import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.domain.repository.DashboardRepository;
import com.aquavitae.infrastructure.entities.*;
import com.aquavitae.infrastructure.mapper.DashboardMapper;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DashboardRepositoryImpl implements DashboardRepository {

    @Inject
    EntityManager em;

    @Override
    public DashboardRiesgo obtenerDashboard() {
        // La consulta ahora selecciona también u.nombre (el nombre textual de la ubicación)
        String jpql = """
            SELECT p, u, ep.indiceHidrico, u.nombre
            FROM PlantaEntity p
            JOIN p.ubicacion u
            LEFT JOIN EstadoPlantaEntity ep ON ep.planta = p
                AND ep.fechaRegistro = (
                    SELECT MAX(e2.fechaRegistro)
                    FROM EstadoPlantaEntity e2
                    WHERE e2.planta = p
                )
            WHERE p.activa = true
            """;

        List<Object[]> rows = em.createQuery(jpql, Object[].class).getResultList();

        List<PlantaRiesgo> plantas = rows.stream()
                .map(row -> DashboardMapper.toPlantaConRiesgo(
                        (PlantaEntity) row[0],
                        (UbicacionEntity) row[1],
                        (Double) row[2],
                        (String) row[3]
                ))
                .collect(Collectors.toList());

        return DashboardMapper.toDashboardRiesgo(plantas);
    }
}