package com.aquavitae.domain.service;

import com.aquavitae.domain.models.ProyeccionHidrica;
import com.aquavitae.domain.models.ProyeccionHidrica.PuntoProyeccion;

public class CalcularDiasHastaUmbralService {

    public static int calcular(ProyeccionHidrica proyeccion, float umbralAlerta) {
        float umbralPorcentaje = umbralAlerta * 100f;

        for (PuntoProyeccion punto : proyeccion.getPuntos()) {
            if (punto.getValor() >= umbralPorcentaje) {
                return punto.getDia();
            }
        }

        return -1;
    }
}
