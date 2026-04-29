package com.aquavitae.domain.models;

public class ResumenRiesgo {

        private final long alto;
        private final long medio;
        private final long bajo;

        public ResumenRiesgo(long alto, long medio, long bajo) {
            this.alto = alto;
            this.medio = medio;
            this.bajo = bajo;
        }

        public long getAlto() { return alto; }
        public long getMedio() { return medio; }
        public long getBajo() { return bajo; }
}
