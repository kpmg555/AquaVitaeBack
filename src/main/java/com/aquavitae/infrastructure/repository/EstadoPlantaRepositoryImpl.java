package com.aquavitae.infrastructure.repository;

import com.aquavitae.domain.repository.EstadoPlantaRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import com.aquavitae.infrastructure.entities.*;

import java.time.LocalDateTime;

@ApplicationScoped
public class EstadoPlantaRepositoryImpl implements EstadoPlantaRepository, PanacheRepositoryBase<EstadoPlantaEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public void guardarEstado(int plantaId, Double indiceHidrico, LocalDateTime fecha) {
        EstadoPlantaEntity estado = new EstadoPlantaEntity();
        PlantaEntity plantaRef = em.getReference(PlantaEntity.class, plantaId);
        estado.setPlanta(plantaRef);
        estado.setIndiceHidrico(indiceHidrico);
        estado.setFechaRegistro(fecha);
        estado.setTipoDato("pronostico_openmeteo");
        estado.setNivelAgua(indiceHidrico * 100);
        persist(estado);
    }
}