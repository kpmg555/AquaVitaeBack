package com.aquavitae.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ClimaticResponse {
    private Current current;
    private Hourly hourly;

    public Current getCurrent() { return current; }
    public void setCurrent(Current current) { this.current = current; }
    public Hourly getHourly() { return hourly; }
    public void setHourly(Hourly hourly) { this.hourly = hourly; }

    public static class Current {
        private Double temperature2m;
        private Double rain;
        private Double evapotranspiration;

        @JsonProperty("temperature_2m")
        public Double getTemperature2m() { return temperature2m; }
        public void setTemperature2m(Double temperature2m) { this.temperature2m = temperature2m; }

        public Double getRain() { return rain; }
        public void setRain(Double rain) { this.rain = rain; }

        public Double getEvapotranspiration() { return evapotranspiration; }
        public void setEvapotranspiration(Double evapotranspiration) { this.evapotranspiration = evapotranspiration; }
    }

    public static class Hourly {
        @JsonProperty("soil_moisture_0_to_1cm")
        private Double[] soilMoisture0To1cm;

        @JsonProperty("soil_moisture_1_to_3cm")
        private Double[] soilMoisture1To3cm;

        @JsonProperty("soil_moisture_3_to_9cm")
        private Double[] soilMoisture3To9cm;

        public Double[] getSoilMoisture0To1cm() { return soilMoisture0To1cm; }
        public void setSoilMoisture0To1cm(Double[] soilMoisture0To1cm) { this.soilMoisture0To1cm = soilMoisture0To1cm; }
        public Double[] getSoilMoisture1To3cm() { return soilMoisture1To3cm; }
        public void setSoilMoisture1To3cm(Double[] soilMoisture1To3cm) { this.soilMoisture1To3cm = soilMoisture1To3cm; }
        public Double[] getSoilMoisture3To9cm() { return soilMoisture3To9cm; }
        public void setSoilMoisture3To9cm(Double[] soilMoisture3To9cm) { this.soilMoisture3To9cm = soilMoisture3To9cm; }
    }
}