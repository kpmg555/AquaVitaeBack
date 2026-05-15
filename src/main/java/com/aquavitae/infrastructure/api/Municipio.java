package com.aquavitae.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Municipio {

    @JsonProperty("muni")
    private String nombreMunicipio;

    @JsonProperty("lat")
    private Double latitud;

    @JsonProperty("lon")
    private Double longitud;

    @JsonProperty("tmax")
    private Double temperaturaMax;

    @JsonProperty("tmin")
    private Double temperaturaMin;

    @JsonProperty("prec")
    private Double precipitacion;

    @JsonProperty("hr")
    private Double humedadRelativa;

    public String getNombreMunicipio() { return nombreMunicipio; }
    public void setNombreMunicipio(String nombreMunicipio) { this.nombreMunicipio = nombreMunicipio; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public Double getTemperaturaMax() { return temperaturaMax; }
    public void setTemperaturaMax(Double temperaturaMax) { this.temperaturaMax = temperaturaMax; }

    public Double getTemperaturaMin() { return temperaturaMin; }
    public void setTemperaturaMin(Double temperaturaMin) { this.temperaturaMin = temperaturaMin; }

    public Double getPrecipitacion() { return precipitacion; }
    public void setPrecipitacion(Double precipitacion) { this.precipitacion = precipitacion; }

    public Double getHumedadRelativa() { return humedadRelativa; }
    public void setHumedadRelativa(Double humedadRelativa) { this.humedadRelativa = humedadRelativa; }
}