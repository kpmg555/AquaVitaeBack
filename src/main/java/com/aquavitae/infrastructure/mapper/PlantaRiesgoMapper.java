package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.infrastructure.entities.PlantaEntity;

public class PlantaRiesgoMapper {
    public static PlantaRiesgo toDomain(PlantaEntity entity,
                                        Float indiceActual,
                                        Float indiceHace7Dias,
                                        Boolean tieneAlertaCritica) {
        PlantaRiesgo m = new PlantaRiesgo();
        m.setId(entity.getId());
        m.setNombre(entity.getNombre());
        if (entity.getUbicacion() != null) {
            m.setUbicacionNombre(entity.getUbicacion().getNombre());
            m.setLatitud(entity.getUbicacion().getLatitud());
            m.setLongitud(entity.getUbicacion().getLongitud());
        }
        m.setIndiceHidrico(indiceActual);
        m.setIndiceHace7Dias(indiceHace7Dias);
        m.setTieneAlertaCritica(tieneAlertaCritica != null && tieneAlertaCritica);
        return m;
    }
}
