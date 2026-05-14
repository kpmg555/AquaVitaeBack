package com.aquavitae.domain.service;

import com.aquavitae.domain.models.FactorEvaluacion;

import java.math.BigDecimal;
import java.util.List;

public class EvaluarFactoresService {

    public static List<FactorEvaluacion> evaluar(float indice, BigDecimal costoOperacion,
                                                  int diasReaperturaMin, int diasReaperturaMax) {
        int disponibilidad = puntosDisponibilidad(indice);
        int costos         = puntosCosto(costoOperacion);
        int tiempo         = puntosTiempo(diasReaperturaMax);

        return List.of(
            new FactorEvaluacion("Disponibilidad hídrica",  "water",  disponibilidad, "#2563eb"),
            new FactorEvaluacion("Costos operativos",       "money",  costos,         "#2ea36b"),
            new FactorEvaluacion("Tiempo de reactivación",  "clock",  tiempo,         "#e89923"),
            new FactorEvaluacion("Regulación ambiental",    "shield", 4,              "#7c3aed"),
            new FactorEvaluacion("Distancia logística",     "truck",  3,              "#d97706")
        );
    }

    private static int puntosDisponibilidad(float indice) {
        if (indice < 0.25f) return 5;
        if (indice < 0.45f) return 4;
        if (indice < 0.65f) return 3;
        if (indice < 0.80f) return 2;
        return 1;
    }

    private static int puntosCosto(BigDecimal costoOperacion) {
        if (costoOperacion == null) return 3;
        double c = costoOperacion.doubleValue();
        if (c < 12_000_000)  return 5;
        if (c < 16_000_000)  return 4;
        if (c < 20_000_000)  return 3;
        if (c < 27_000_000)  return 2;
        return 1;
    }

    private static int puntosTiempo(int diasMax) {
        if (diasMax <= 30) return 5;
        if (diasMax <= 45) return 4;
        if (diasMax <= 55) return 3;
        if (diasMax <= 65) return 2;
        return 1;
    }
}
