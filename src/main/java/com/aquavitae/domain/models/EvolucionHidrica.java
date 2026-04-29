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
            private LocalDate fecha;
            private float valorPromedio;

            public PuntoDiario(LocalDate fecha, float valorPromedio) {
                this.fecha = fecha;
                this.valorPromedio = valorPromedio;
            }

            public LocalDate getFecha() { return fecha; }
            public void setFecha(LocalDate fecha) { this.fecha = fecha; }

            public float getValorPromedio() { return valorPromedio; }
            public void setValorPromedio(float valorPromedio) { this.valorPromedio = valorPromedio; }
        }
}
