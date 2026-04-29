package com.aquavitae.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import com.aquavitae.domain.models.UbicacionClima;
import com.aquavitae.domain.repository.PlantaQueryRepository;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class PlantaRepositoryImpl implements PlantaQueryRepository, PanacheRepositoryBase<PlantaEntity, Integer> {

    @Override
    public List<UbicacionClima> obtenerUbicacionesActivas() {
        List<PlantaEntity> plantas = find("activa", true).list();
        return plantas.stream()
                .map(p -> new UbicacionClima(
                        p.getId(),
                         p.getUbicacion().getLatitud(),
                        p.getUbicacion().getLongitud(),
                        p.getUbicacion().getElevation() != null ? p.getUbicacion().getElevation() : 0
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Planta> findAllActivas() {
        return List.of();
    }
}