package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.domain.models.DashboardRiesgo;
import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.domain.models.ResumenRiesgo;
import com.aquavitae.domain.repository.DashboardRepository;
import com.aquavitae.infrastructure.entities.*;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DashboardRepositoryImpl implements DashboardRepository {

    @Inject
    EntityManager em;

    @Override
    public DashboardRiesgo obtenerDashboard() {
        String jpql = """
            SELECT p, u, ep.indiceHidrico
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

        List<Object[]> filas = em.createQuery(jpql, Object[].class).getResultList();

        List<PlantaRiesgo> plantas = filas.stream().map(row -> {
            PlantaEntity planta = (PlantaEntity) row[0];
            UbicacionEntity ubicacion = (UbicacionEntity) row[1];
            float indice = (float)row[2];
            String nivel = clasificarRiesgo(indice);

            return new PlantaRiesgo(
                    planta.getId(),
                    planta.getNombre(),
                    ubicacion.getLatitud(),
                    ubicacion.getLongitud(),
                    indice,
                    nivel
            );
        }).collect(Collectors.toList());

        long alto = plantas.stream().filter(p -> "ALTO".equals(p.getNivelRiesgo())).count();
        long medio = plantas.stream().filter(p -> "MEDIO".equals(p.getNivelRiesgo())).count();
        long bajo = plantas.stream().filter(p -> "BAJO".equals(p.getNivelRiesgo())).count();

        return new DashboardRiesgo(plantas, new ResumenRiesgo(alto, medio, bajo));
    }

    private String clasificarRiesgo(double indice) {
        if (indice <= 0.3) return "ALTO";
        if (indice <= 0.6) return "MEDIO";
        return "BAJO";
    }
}