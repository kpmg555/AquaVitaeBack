package com.aquavitae.domain.models;

import java.time.LocalDate;
import java.util.List;

public class EvolucionHidrica {

        private List<PuntoDiario> puntos;

        public EvolucionHidrica(List<PuntoDiario> puntos) {
            this.puntos = puntos;
        }

        public List<PuntoDiario> getPuntos() { return puntos; }
        public void setPuntos(List<PuntoDiario> puntos) { this.puntos = puntos; }

        public static class PuntoDiario {
            private final LocalDate fecha;
            private final float valorPromedio;

            public PuntoDiario(LocalDate fecha, float valorPromedio) {
                this.fecha = fecha;
                this.valorPromedio = valorPromedio;
            }

            public LocalDate getFecha() { return fecha; }

            public float getValorPromedio() { return valorPromedio; }
        }
}
