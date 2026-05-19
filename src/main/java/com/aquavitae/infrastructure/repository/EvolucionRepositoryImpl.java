package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.domain.models.EvolucionHidrica;
import com.aquavitae.domain.repository.EvolucionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class EvolucionRepositoryImpl implements EvolucionRepository {

    @Inject
    EntityManager em;

    @Override
    public EvolucionHidrica obtenerEvolucion(int dias) {
        LocalDate desde = LocalDate.now().minusDays(dias);
        String jpql = """
            SELECT FUNCTION('DATE', ep.fechaRegistro), AVG(ep.indiceHidrico)
            FROM EstadoPlantaEntity ep
            WHERE ep.fechaRegistro >= :desde
            GROUP BY FUNCTION('DATE', ep.fechaRegistro)
            ORDER BY FUNCTION('DATE', ep.fechaRegistro)
            """;
        List<Object[]> rows = em.createQuery(jpql, Object[].class)
                .setParameter("desde", desde.atStartOfDay())
                .getResultList();

        List<EvolucionHidrica.PuntoDiario> puntos = rows.stream()
                .map(row -> new EvolucionHidrica.PuntoDiario(
                        ((java.sql.Date) row[0]).toLocalDate(),
                        ((Number) row[1]).floatValue()
                ))
                .collect(Collectors.toList());

        return new EvolucionHidrica(puntos);
    }
}