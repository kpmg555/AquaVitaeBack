package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.domain.models.EscenarioRecuperacion;
import com.aquavitae.domain.models.KpiSimulacion;
import com.aquavitae.domain.models.ProyeccionHidrica;
import com.aquavitae.domain.repository.SimulacionRepository;
import com.aquavitae.domain.service.GenerarProyeccionService;
import com.aquavitae.domain.service.GenerarRecuperacionService;
import com.aquavitae.infrastructure.entities.EstadoPlantaEntity;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import com.aquavitae.infrastructure.mapper.SimulacionMapper;

@ApplicationScoped
public class SimulacionRepositoryImpl implements SimulacionRepository {

    @Inject
    EntityManager em;

    @Override
    public KpiSimulacion obtenerKpis(Integer plantaId) {
        EstadoPlantaEntity estado = obtenerUltimoEstado(plantaId);
        PlantaEntity planta = em.find(PlantaEntity.class, plantaId);
        return SimulacionMapper.toKpiSimulacion(estado, planta);
    }

    @Override
    public ProyeccionHidrica obtenerProyeccion(Integer plantaId, int dias) {
        EstadoPlantaEntity estado = obtenerUltimoEstado(plantaId);
        float indice = estado.getIndiceHidrico() != null ? estado.getIndiceHidrico() : 0f;
        return GenerarProyeccionService.generar(indice, dias);
    }

    @Override
    public EscenarioRecuperacion obtenerRecuperacion(Integer plantaId, int dias) {
        EstadoPlantaEntity estado = obtenerUltimoEstado(plantaId);
        float indice = estado.getIndiceHidrico() != null ? estado.getIndiceHidrico() : 0f;
        ProyeccionHidrica proyeccion = GenerarProyeccionService.generar(indice, dias);
        return GenerarRecuperacionService.generar(proyeccion.getPeakValue(), dias);
    }

    private EstadoPlantaEntity obtenerUltimoEstado(Integer plantaId) {
        return em.createQuery("""
                SELECT ep FROM EstadoPlantaEntity ep
                WHERE ep.planta.id = :plantaId
                AND ep.fechaRegistro = (
                    SELECT MAX(e2.fechaRegistro)
                    FROM EstadoPlantaEntity e2
                    WHERE e2.planta.id = :plantaId
                )
                """, EstadoPlantaEntity.class)
                .setParameter("plantaId", plantaId)
                .setMaxResults(1)
                .getSingleResult();
    }
}
