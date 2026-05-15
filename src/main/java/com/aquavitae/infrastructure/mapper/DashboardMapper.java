package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.domain.models.ResumenRiesgo;
import com.aquavitae.domain.models.DashboardRiesgo;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import com.aquavitae.infrastructure.entities.UbicacionEntity;
import java.util.List;

public class DashboardMapper {

    // Ahora recibe también el nombre de la ubicación
    public static PlantaRiesgo toPlantaConRiesgo(PlantaEntity planta, UbicacionEntity ubicacion,
                                                 Double indiceHidrico, String ubicacionNombre) {
        Double indice = (indiceHidrico != null) ? indiceHidrico : 1.0f;
        String nivel = clasificarRiesgo(indice);
        return new PlantaRiesgo(
                planta.getId(),
                planta.getNombre(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                indice,
                nivel,
                ubicacionNombre != null ? ubicacionNombre : ""
        );
    }

    public static DashboardRiesgo toDashboardRiesgo(List<PlantaRiesgo> plantas) {
        long alto = plantas.stream().filter(p -> "ALTO".equals(p.getNivelRiesgo())).count();
        long medio = plantas.stream().filter(p -> "MEDIO".equals(p.getNivelRiesgo())).count();
        long bajo = plantas.stream().filter(p -> "BAJO".equals(p.getNivelRiesgo())).count();
        return new DashboardRiesgo(plantas, new ResumenRiesgo(alto, medio, bajo));
    }

// Clasificación simple basada en el índice hídrico corrregida
    private static String clasificarRiesgo(Double indice) {
        if (indice <= 0.3) return "BAJO";
        if (indice <= 0.6) return "MEDIO";
        return "ALTO";
    }
}
