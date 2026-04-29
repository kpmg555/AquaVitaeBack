//   Coordina repositorios JPA (infraestructura).
//  Facade expone una interfaz que vive en application (RiesgoHidricoFacadePort) y que es implementada aquí.
//  El usecase inyecta la interfaz, no la implementación, lo que permite desacoplar el dominio de la infraestructura.

package com.aquavitae.infrastructure.facade;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.domain.repository.AlertaRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@ApplicationScoped
public class RiesgoHidricoFacade implements RiesgoHidricoFacadePort {

    @Inject PlantaRepository plantaRepository;
    @Inject AlertaRepository alertaRepository;
    @Inject EntityManager    em;

    @Override
    public List<PlantaRiesgo> obtenerPlantasConRiesgo() {
        return plantaRepository.findAllActivasConRiesgo();
    }

    @Override
    public long contarCrisisActivas() {
        return alertaRepository.contarCriticasActivas();
    }

    @Override
    public List<AlertaDominio> obtenerAlertasRecientes(int limite) {
        return alertaRepository.findRecientes(limite);
    }

    @Override
    public Map<String, Float> obtenerEvolucion7Dias() {
        List<Object[]> resultados = em.createQuery(
                        "SELECT CAST(e.fechaRegistro AS date), AVG(e.indiceHidrico) " +
                                "FROM EstadoPlantaEntity e " +
                                "WHERE e.fechaRegistro >= :desde " +
                                "  AND e.tipoDato IN ('pronostico_openmeteo', 'pronostico_smn') " +
                                "GROUP BY CAST(e.fechaRegistro AS date) " +
                                "ORDER BY CAST(e.fechaRegistro AS date) ASC",
                        Object[].class
                )
                .setParameter("desde",
                        java.sql.Timestamp.valueOf(LocalDate.now().minusDays(7).atStartOfDay()))
                .getResultList();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM",
                new Locale("es", "MX"));

        Map<String, Float> evolucion = new LinkedHashMap<>();
        for (Object[] fila : resultados) {
            LocalDate fecha = ((java.sql.Date) fila[0]).toLocalDate();
            evolucion.put(fecha.format(fmt), (Float) fila[1]);
        }
        return evolucion;
    }
}