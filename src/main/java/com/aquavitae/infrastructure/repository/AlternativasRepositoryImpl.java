package com.aquavitae.infrastructure.repository;

import com.aquavitae.domain.models.AlertaOperativa;
import com.aquavitae.domain.models.AlternativaUbicacion;
import com.aquavitae.domain.models.FactorEvaluacion;
import com.aquavitae.domain.repository.AlternativasRepository;
import com.aquavitae.domain.service.ClasificarRiesgoService;
import com.aquavitae.infrastructure.entities.EstadoPlantaEntity;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import com.aquavitae.infrastructure.mapper.AlternativasMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AlternativasRepositoryImpl implements AlternativasRepository {

    @Inject
    EntityManager em;

    @Override
    public AlertaOperativa obtenerAlerta(Integer plantaId) {
        EstadoPlantaEntity estado = obtenerUltimoEstado(plantaId);
        PlantaEntity planta = em.find(PlantaEntity.class, plantaId);
        return AlternativasMapper.toAlerta(estado, planta);
    }

    @Override
    public List<AlternativaUbicacion> obtenerAlternativas(Integer plantaId) {
        List<PlantaEntity> otras = em.createQuery("""
                SELECT p FROM PlantaEntity p
                WHERE p.id <> :plantaId AND p.activa = true
                """, PlantaEntity.class)
                .setParameter("plantaId", plantaId)
                .getResultList();

        List<AlternativaUbicacion> alternativas = otras.stream()
            .map(p -> {
                Optional<EstadoPlantaEntity> estadoOpt = obtenerUltimoEstadoOpt(p.getId());
                if (estadoOpt.isEmpty()) return null;
                EstadoPlantaEntity estado = estadoOpt.get();
                String estadoNombre = p.getUbicacion() != null ? p.getUbicacion().getNombre() : p.getNombre();
                return AlternativasMapper.toAlternativa(estado, p, estadoNombre, false);
            })
            .filter(a -> a != null)
            .sorted(Comparator.comparing(a -> riesgoOrden(a.getRiesgo())))
            .toList();

        // La de menor costo total y mejor riesgo es la recomendada
        return marcarRecomendada(alternativas);
    }

    @Override
    public List<FactorEvaluacion> obtenerFactores(Integer plantaId) {
        EstadoPlantaEntity estado = obtenerUltimoEstado(plantaId);
        PlantaEntity planta = em.find(PlantaEntity.class, plantaId);
        return AlternativasMapper.toFactores(estado, planta);
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

    private Optional<EstadoPlantaEntity> obtenerUltimoEstadoOpt(Integer plantaId) {
        List<EstadoPlantaEntity> result = em.createQuery("""
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
                .getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    private int riesgoOrden(String riesgo) {
        return switch (riesgo) {
            case "bajo"  -> 0;
            case "medio" -> 1;
            default      -> 2;
        };
    }

    private List<AlternativaUbicacion> marcarRecomendada(List<AlternativaUbicacion> lista) {
        if (lista.isEmpty()) return lista;
        // Primera de riesgo bajo con menor costo total
        AlternativaUbicacion mejor = lista.stream()
            .filter(a -> "bajo".equals(a.getRiesgo()))
            .min(Comparator.comparing(a -> a.getCostoTotal()))
            .orElse(lista.get(0));

        return lista.stream()
            .map(a -> a.getPlantaId().equals(mejor.getPlantaId())
                ? new AlternativaUbicacion(a.getPlantaId(), a.getNombre(), a.getEstado(),
                    a.getRiesgo(), a.getRiesgoLabel(), a.getCostoCierre(), a.getCostoApertura(),
                    a.getCostoTotal(), a.getTiempoCierreDias(), a.getTiempoAperturaMin(),
                    a.getTiempoAperturaMax(), true)
                : a)
            .toList();
    }
}
