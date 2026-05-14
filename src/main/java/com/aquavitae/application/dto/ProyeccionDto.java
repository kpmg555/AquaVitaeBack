package com.aquavitae.application.dto;

import java.util.List;

public class ProyeccionDto {

    private final List<PuntoDto> puntos;
    private final List<PuntoDto> bandaSuperior;
    private final List<PuntoDto> bandaInferior;
    private final int startDay;
    private final int peakDay;
    private final float peakValue;

    public ProyeccionDto(List<PuntoDto> puntos,
                         List<PuntoDto> bandaSuperior,
                         List<PuntoDto> bandaInferior,
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

    public List<PuntoDto> getPuntos() { return puntos; }
    public List<PuntoDto> getBandaSuperior() { return bandaSuperior; }
    public List<PuntoDto> getBandaInferior() { return bandaInferior; }
    public int getStartDay() { return startDay; }
    public int getPeakDay() { return peakDay; }
    public float getPeakValue() { return peakValue; }

    public static class PuntoDto {
        private final int dia;
        private final float valor;

        public PuntoDto(int dia, float valor) {
            this.dia = dia;
            this.valor = valor;
        }

        public int getDia() { return dia; }
        public float getValor() { return valor; }
    }
}
