package com.aquavitae.domain.models;

import java.util.UUID;

public class DatosClimaticos {
        private UUID id;
        private float precipitacionMm;
        private float  humedadSuelo;
        private float evapotranspiracion;

        public DatosClimaticos(UUID id, float precipitacionMm,
                               float humedadSuelo, float evapotranspiracion) {
            this.id = id;
            this.precipitacionMm = precipitacionMm;
            this.humedadSuelo = humedadSuelo;
            this.evapotranspiracion = evapotranspiracion;
        }

        public UUID getId() {return id;}
        public void setId(UUID id) {this.id = id;}

        public float getPrecipitacionMm() {return precipitacionMm;}
        public void setPrecipitacionMm(float precipitacionMm) {this.precipitacionMm = precipitacionMm;}

        public float getHumedadSuelo() {return humedadSuelo;}
        public void setHumedadSuelo(float humedadSuelo) {this.humedadSuelo = humedadSuelo;}

        public float getEvapotranspiracion() {return evapotranspiracion;}
        public void setEvapotranspiracion(float evapotranspiracion) {this.evapotranspiracion = evapotranspiracion;}
}
