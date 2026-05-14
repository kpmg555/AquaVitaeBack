package com.aquavitae.domain.models;

import java.util.List;

public class ProyeccionHidrica {

    private final List<PuntoProyeccion> puntos;
    private final List<PuntoProyeccion> bandaSuperior;
    private final List<PuntoProyeccion> bandaInferior;
    private final int startDay;
    private final int peakDay;
    private final float peakValue;

    public ProyeccionHidrica(List<PuntoProyeccion> puntos,
                             List<PuntoProyeccion> bandaSuperior,
                             List<PuntoProyeccion> bandaInferior,
                             int startDay,
                             int peakDay,
                             float peakValue) {
        this.puntos = puntos;
        this.bandaSuperior = bandaSuperior;
        this.bandaInferior = bandaInferior;
        this.startDay = startDay;
        this.peakDay = peakDay;
        this.peakValue = peakValue;
    }

    public List<PuntoProyeccion> getPuntos() { return puntos; }
    public List<PuntoProyeccion> getBandaSuperior() { return bandaSuperior; }
    public List<PuntoProyeccion> getBandaInferior() { return bandaInferior; }
    public int getStartDay() { return startDay; }
    public int getPeakDay() { return peakDay; }
    public float getPeakValue() { return peakValue; }

    public static class PuntoProyeccion {
        private final int dia;
        private final float valor;

        public PuntoProyeccion(int dia, float valor) {
            this.dia = dia;
            this.valor = valor;
        }

        public int getDia() { return dia; }
        public float getValor() { return valor; }
    }
}
