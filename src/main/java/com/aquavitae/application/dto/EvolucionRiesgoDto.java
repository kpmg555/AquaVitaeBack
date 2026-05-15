package com.aquavitae.application.dto;

import java.time.LocalDate;
import java.util.List;

public class EvolucionRiesgoDto {
    private final List<PuntoDto> puntos;

    public EvolucionRiesgoDto(List<PuntoDto> puntos) { this.puntos = puntos; }
    public List<PuntoDto> getPuntos() { return puntos; }

    public static class PuntoDto {
        private final LocalDate fecha;
        private final Double valorPromedio;

        public PuntoDto(LocalDate fecha, Double valorPromedio) {
            this.fecha = fecha;
            this.valorPromedio = (Double) valorPromedio;
        }
        public LocalDate getFecha() { return fecha; }
        public Double getValorPromedio() { return valorPromedio; }
    }
}