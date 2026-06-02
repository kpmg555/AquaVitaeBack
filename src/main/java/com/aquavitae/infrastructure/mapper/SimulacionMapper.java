package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.KpiSimulacion;
import com.aquavitae.domain.service.CalcularDiasHastaUmbralService;
import com.aquavitae.domain.service.CalcularPerdidaEconomicaService;
import com.aquavitae.domain.service.GenerarProyeccionService;
import com.aquavitae.domain.models.ProyeccionHidrica;
import com.aquavitae.infrastructure.entities.EstadoPlantaEntity;
import com.aquavitae.infrastructure.entities.PlantaEntity;

public class SimulacionMapper {

    public static KpiSimulacion toKpiSimulacion(EstadoPlantaEntity estado, PlantaEntity planta) {
        float indice = estado.getIndiceHidrico() != null ? estado.getIndiceHidrico().floatValue() : 0f;
        float umbral = planta.getUmbralAlerta() != null ? planta.getUmbralAlerta().floatValue() : 0.75f;

        ProyeccionHidrica proyeccion = GenerarProyeccionService.generar(indice, 90);
        int dias = CalcularDiasHastaUmbralService.calcular(proyeccion, umbral);
        double perdida = CalcularPerdidaEconomicaService.calcular(dias, planta.getCostoOperacionDiariaMxn());
        float probabilidad = calcularProbabilidad(indice);

        return new KpiSimulacion(indice, dias, probabilidad, perdida);
    }

    private static float calcularProbabilidad(float indice) {
        if (indice >= 0.75f) return 0.85f;
        if (indice >= 0.50f) return 0.55f;
        if (indice >= 0.30f) return 0.30f;
        return 0.10f;
    }
}
