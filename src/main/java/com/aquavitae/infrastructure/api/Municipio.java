package com.aquavitae.infrastructure.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Municipio {

    @JsonProperty("muni")
    private String nombreMunicipio;

    @JsonProperty("lat")
    private double latitud;

    @JsonProperty("lon")
    private double longitud;

    @JsonProperty("tmax")
    private float temperaturaMax;

    @JsonProperty("tmin")
    private float temperaturaMin;

    @JsonProperty("prec")
    private float precipitacion;

    @JsonProperty("hr")
    private float humedadRelativa;

    public String getNombreMunicipio() { return nombreMunicipio; }
    public void setNombreMunicipio(String nombreMunicipio) { this.nombreMunicipio = nombreMunicipio; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public float getTemperaturaMax() { return temperaturaMax; }
    public void setTemperaturaMax(float temperaturaMax) { this.temperaturaMax = temperaturaMax; }

    public float getTemperaturaMin() { return temperaturaMin; }
    public void setTemperaturaMin(float temperaturaMin) { this.temperaturaMin = temperaturaMin; }

    public float getPrecipitacion() { return precipitacion; }
    public void setPrecipitacion(float precipitacion) { this.precipitacion = precipitacion; }

    public float getHumedadRelativa() { return humedadRelativa; }
    public void setHumedadRelativa(float humedadRelativa) { this.humedadRelativa = humedadRelativa; }
}