package com.aquavitae.domain.models;

import java.util.List;

public class EscenarioRecuperacion {

    private final List<PuntoRecuperacion> conIntervencion;
    private final List<PuntoRecuperacion> sinIntervencion;

    public EscenarioRecuperacion(List<PuntoRecuperacion> conIntervencion,
                                 List<PuntoRecuperacion> sinIntervencion) {
        this.conIntervencion = conIntervencion;
        this.sinIntervencion = sinIntervencion;
    }

    public List<PuntoRecuperacion> getConIntervencion() { return conIntervencion; }
    public List<PuntoRecuperacion> getSinIntervencion() { return sinIntervencion; }

    public static class PuntoRecuperacion {
        private final int dia;
        private final float valor;

        public PuntoRecuperacion(int dia, float valor) {
            this.dia = dia;
            this.valor = valor;
        }

        public int getDia() { return dia; }
        public float getValor() { return valor; }
    }
}
