package com.aquavitae.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import com.aquavitae.domain.repository.EstadoPlantaRepository;
import com.aquavitae.infrastructure.entities.*;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class EstadoPlantaRepositoryImpl implements EstadoPlantaRepository, PanacheRepositoryBase<EstadoPlantaEntity, Integer> {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public void guardarEstado(UUID plantaId, float indiceHidrico, LocalDateTime fecha) {
        EstadoPlantaEntity estado = new EstadoPlantaEntity();
        PlantaEntity plantaRef = em.getReference(PlantaEntity.class, plantaId);
        estado.setPlanta(plantaRef);
        estado.setIndiceHidrico(indiceHidrico);
        estado.setFechaRegistro(fecha);
        persist(estado);
    }
}