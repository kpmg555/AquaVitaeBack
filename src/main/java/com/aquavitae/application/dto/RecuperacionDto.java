package com.aquavitae.application.dto;

import java.util.List;

public class RecuperacionDto {

    private final List<PuntoDto> conIntervencion;
    private final List<PuntoDto> sinIntervencion;

    public RecuperacionDto(List<PuntoDto> conIntervencion,
                           List<PuntoDto> sinIntervencion) {
        this.conIntervencion = conIntervencion;
        this.sinIntervencion = sinIntervencion;
    }

    public List<PuntoDto> getConIntervencion() { return conIntervencion; }
    public List<PuntoDto> getSinIntervencion() { return sinIntervencion; }

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
