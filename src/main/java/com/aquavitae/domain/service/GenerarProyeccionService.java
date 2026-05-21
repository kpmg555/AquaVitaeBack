package com.aquavitae.domain.service;

import com.aquavitae.domain.models.ProyeccionHidrica;
import com.aquavitae.domain.models.ProyeccionHidrica.PuntoProyeccion;

import java.util.ArrayList;
import java.util.List;

public class GenerarProyeccionService {

    private static final float BANDA_MARGEN = 8f;

    public static ProyeccionHidrica generar(double indiceInicial, int dias) {

        int startDay = (int) (dias * 0.15);
        int peakDay  = (int) (dias * 0.55);
        float peakValue = (float) Math.min(95.0, indiceInicial * 100 * 1.4);

        List<PuntoProyeccion> puntos       = new ArrayList<>();
        List<PuntoProyeccion> bandaSuperior = new ArrayList<>();
        List<PuntoProyeccion> bandaInferior = new ArrayList<>();

        for (int d = 0; d <= dias; d += 2) {
            float v;
            if (d < startDay) {
                v = (float) (indiceInicial * 100 + (d / (float) startDay) * 8);
            } else if (d <= peakDay) {
                float t = (d - startDay) / (float) (peakDay - startDay);
                v = (float) (indiceInicial * 100 + (peakValue - indiceInicial * 100) * (1 - Math.pow(1 - t, 2)));
            } else {
                float t = (d - peakDay) / (float) (dias - peakDay);
                v = peakValue - (peakValue - 30f) * (float) Math.pow(t, 1.4);
            }

            v = Math.max(8f, Math.min(95f, v));

            puntos.add(new PuntoProyeccion(d, v));
            bandaSuperior.add(new PuntoProyeccion(d, Math.min(98f, v + BANDA_MARGEN)));
            bandaInferior.add(new PuntoProyeccion(d, Math.max(4f,  v - BANDA_MARGEN)));
        }

        return new ProyeccionHidrica(puntos, bandaSuperior, bandaInferior, startDay, peakDay, peakValue);
    }
}
