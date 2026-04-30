package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.domain.models.ResumenRiesgo;
import com.aquavitae.domain.models.DashboardRiesgo;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import com.aquavitae.infrastructure.entities.UbicacionEntity;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardMapper {

    public static PlantaRiesgo toPlantaConRiesgo(PlantaEntity planta, UbicacionEntity ubicacion, Float indiceHidrico) {
        float indice = (indiceHidrico != null) ? indiceHidrico : (float) 1.0;
        String nivel = clasificarRiesgo(indice);
        return new PlantaRiesgo(
                planta.getId(),
                planta.getNombre(),
                ubicacion.getLatitud(),
                ubicacion.getLongitud(),
                indice,
                nivel
        );
    }

    public static DashboardRiesgo toDashboardRiesgo(List<PlantaRiesgo> plantas) {
        long alto = plantas.stream().filter(p -> "ALTO".equals(p.getNivelRiesgo())).count();
        long medio = plantas.stream().filter(p -> "MEDIO".equals(p.getNivelRiesgo())).count();
        long bajo = plantas.stream().filter(p -> "BAJO".equals(p.getNivelRiesgo())).count();
        return new DashboardRiesgo(plantas, new ResumenRiesgo(alto, medio, bajo));
    }

    private static String clasificarRiesgo(double indice) {
        if (indice <= 0.3) return "ALTO";
        if (indice <= 0.6) return "MEDIO";
        return "BAJO";
    }
}