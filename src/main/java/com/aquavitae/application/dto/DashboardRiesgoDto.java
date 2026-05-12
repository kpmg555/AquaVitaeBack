package com.aquavitae.application.dto;

import java.util.List;
import java.util.UUID;

public class DashboardRiesgoDto {
    private final List<PlantaRiesgoDto> plantas;
    private final ResumenDto resumen;

    private DashboardRiesgoDto(Builder builder) {
        this.plantas = builder.plantas;
        this.resumen = builder.resumen;
    }

    public List<PlantaRiesgoDto> getPlantas() { return plantas; }
    public ResumenDto getResumen() { return resumen; }

    public static class Builder {
        private List<PlantaRiesgoDto> plantas;
        private ResumenDto resumen;

        public Builder plantas(List<PlantaRiesgoDto> plantas) {
            this.plantas = plantas;
            return this;
        }
        public Builder resumen(ResumenDto resumen) {
            this.resumen = resumen;
            return this;
        }
        public DashboardRiesgoDto build() {
            return new DashboardRiesgoDto(this);
        }
    }

    public static class PlantaRiesgoDto {
        private final Integer id;
        private final String nombre;
        private final float latitud;
        private final float longitud;
        private final float indiceHidrico;
        private final String nivelRiesgo;
        private final String ubicacionNombre;   // NUEVO

        public PlantaRiesgoDto(Integer id, String nombre, float latitud,
                               float longitud, float indiceHidrico,
                               String nivelRiesgo, String ubicacionNombre) {
            this.id = id;
            this.nombre = nombre;
            this.latitud = latitud;
            this.longitud = longitud;
            this.indiceHidrico = indiceHidrico;
            this.nivelRiesgo = nivelRiesgo;
            this.ubicacionNombre = ubicacionNombre;
        }

        public Integer getIdPlanta() { return id; }
        public String getNombrePlanta() { return nombre; }
        public float getLatitud() { return latitud; }
        public float getLongitud() { return longitud; }
        public float getIndiceHidrico() { return indiceHidrico; }
        public String getNivelRiesgo() { return nivelRiesgo; }
        public String getUbicacionNombre() { return ubicacionNombre; }
    }

    public static class ResumenDto {
        private final long alto;
        private final long medio;
        private final long bajo;

        public ResumenDto(long alto, long medio, long bajo) {
            this.alto = alto;
            this.medio = medio;
            this.bajo = bajo;
        }

        public long getAlto() { return alto; }
        public long getMedio() { return medio; }
        public long getBajo() { return bajo; }
    }
}