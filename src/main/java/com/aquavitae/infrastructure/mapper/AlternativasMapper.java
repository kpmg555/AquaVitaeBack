package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.AlertaOperativa;
import com.aquavitae.domain.models.AlternativaUbicacion;
import com.aquavitae.domain.models.FactorEvaluacion;
import com.aquavitae.domain.service.ClasificarRiesgoService;
import com.aquavitae.domain.service.EvaluarFactoresService;
import com.aquavitae.domain.service.GenerarProyeccionService;
import com.aquavitae.domain.service.CalcularDiasHastaUmbralService;
import com.aquavitae.infrastructure.entities.EstadoPlantaEntity;
import com.aquavitae.infrastructure.entities.PlantaEntity;

import java.math.BigDecimal;
import java.util.List;

public class AlternativasMapper {

    public static AlertaOperativa toAlerta(EstadoPlantaEntity estado, PlantaEntity planta) {
        float indice = estado.getIndiceHidrico() != null ? estado.getIndiceHidrico().floatValue() : 0f;
        float umbral = planta.getUmbralAlerta() != null ? planta.getUmbralAlerta().floatValue() : 0.75f;

        var proyeccion = GenerarProyeccionService.generar(indice, 90);
        int diasCierre = CalcularDiasHastaUmbralService.calcular(proyeccion, umbral);

        BigDecimal costoApertura = planta.getCostoAperturaMxn() != null
            ? planta.getCostoAperturaMxn() : BigDecimal.ZERO;

        BigDecimal costoOpDiaria = planta.getCostoOperacionDiariaMxn() != null
            ? planta.getCostoOperacionDiariaMxn() : BigDecimal.ZERO;
        // diasCierre=-1 significa sin riesgo en 90 días; evita multiplicar por negativo
        int diasParaCosto = Math.max(0, diasCierre);
        BigDecimal costoOpEstimada = costoOpDiaria.multiply(BigDecimal.valueOf(diasParaCosto));

        int diasMin = planta.getDiasReaperturaMin() != null ? planta.getDiasReaperturaMin() : 30;
        int diasMax = planta.getDiasReaperturaMax() != null ? planta.getDiasReaperturaMax() : 60;

        return new AlertaOperativa(indice, diasCierre, costoApertura, costoOpEstimada,
                                   diasMin, diasMax, planta.getNombre());
    }

    public static AlternativaUbicacion toAlternativa(EstadoPlantaEntity estado, PlantaEntity planta,
                                                      String estado_nombre, boolean recomendada) {
        float indice = estado.getIndiceHidrico() != null ? estado.getIndiceHidrico().floatValue() : 0f;
        String riesgo = ClasificarRiesgoService.clasificar(indice);
        String riesgoLabel = ClasificarRiesgoService.label(riesgo);

        BigDecimal costoCierre  = planta.getCostoCierreMxn()  != null ? planta.getCostoCierreMxn()  : BigDecimal.ZERO;
        BigDecimal costoApertura = planta.getCostoAperturaMxn() != null ? planta.getCostoAperturaMxn() : BigDecimal.ZERO;
        BigDecimal costoTotal   = costoCierre.add(costoApertura);

        int diasMin = planta.getDiasReaperturaMin() != null ? planta.getDiasReaperturaMin() : 30;
        int diasMax = planta.getDiasReaperturaMax() != null ? planta.getDiasReaperturaMax() : 60;
        int tiempoCierre = Math.round(diasMin * 0.7f);

        return new AlternativaUbicacion(planta.getId(), planta.getNombre(), estado_nombre,
                                        riesgo, riesgoLabel, costoCierre, costoApertura, costoTotal,
                                        tiempoCierre, diasMin, diasMax, recomendada);
    }

    public static List<FactorEvaluacion> toFactores(EstadoPlantaEntity estado, PlantaEntity planta) {
        float indice = estado.getIndiceHidrico() != null ? estado.getIndiceHidrico().floatValue() : 0f;
        BigDecimal costoOp = planta.getCostoOperacionDiariaMxn();
        int diasMin = planta.getDiasReaperturaMin() != null ? planta.getDiasReaperturaMin() : 30;
        int diasMax = planta.getDiasReaperturaMax() != null ? planta.getDiasReaperturaMax() : 60;
        return EvaluarFactoresService.evaluar(indice, costoOp, diasMin, diasMax);
    }
}
