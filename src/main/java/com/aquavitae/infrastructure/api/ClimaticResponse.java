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
        private float temperature2m;
        private float rain;
        private float evapotranspiration;

        @JsonProperty("temperature_2m")
        public float getTemperature2m() { return temperature2m; }
        public void setTemperature2m(float temperature2m) { this.temperature2m = temperature2m; }

        public float getRain() { return rain; }
        public void setRain(float rain) { this.rain = rain; }

        public float getEvapotranspiration() { return evapotranspiration; }
        public void setEvapotranspiration(float evapotranspiration) { this.evapotranspiration = evapotranspiration; }
    }

    public static class Hourly {
        @JsonProperty("soil_moisture_0_to_1cm")
        private float[] soilMoisture0To1cm;

        @JsonProperty("soil_moisture_1_to_3cm")
        private float[] soilMoisture1To3cm;

        @JsonProperty("soil_moisture_3_to_9cm")
        private float[] soilMoisture3To9cm;

        public float[] getSoilMoisture0To1cm() { return soilMoisture0To1cm; }
        public void setSoilMoisture0To1cm(float[] soilMoisture0To1cm) { this.soilMoisture0To1cm = soilMoisture0To1cm; }
        public float[] getSoilMoisture1To3cm() { return soilMoisture1To3cm; }
        public void setSoilMoisture1To3cm(float[] soilMoisture1To3cm) { this.soilMoisture1To3cm = soilMoisture1To3cm; }
        public float[] getSoilMoisture3To9cm() { return soilMoisture3To9cm; }
        public void setSoilMoisture3To9cm(float[] soilMoisture3To9cm) { this.soilMoisture3To9cm = soilMoisture3To9cm; }
    }
}