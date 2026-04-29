package com.aquavitae.domain.models.;

import java.util.UUID;

public class PlantaRiesgo {
    private UUID id;
    private String nombrePlanta;
    private float latitud;
    private float longitud;
    private float indiceHidrico;
    private String nivelRiesgo;

    public PlantaRiesgo(UUID id, String nombrePlanta,
                           float latitud, float longitud,
                           float indiceHidrico, String nivelRiesgo) {
        this.id = id;
        this.nombrePlanta = nombrePlanta;
        this.latitud = latitud;
        this.longitud = longitud;
        this.indiceHidrico = indiceHidrico;
        this.nivelRiesgo = nivelRiesgo;
    }

    public UUID getId() {return id;}
    public void setId(UUID id) { this.id = id; }

    public String getNombrePlanta() { return nombrePlanta; }
    public void setNombrePlanta(String nombrePlanta) { this.nombrePlanta = nombrePlanta; }

    public float getLatitud() { return latitud; }
    public void setLatitud(float latitud) { this.latitud = latitud; }

    public float getLongitud() { return longitud; }
    public void setLongitud(float longitud) { this.longitud = longitud; }

    public float getIndiceHidrico() { return indiceHidrico; }
    public void setIndiceHidrico(float indiceHidrico) { this.indiceHidrico = indiceHidrico; }

    public String getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(String nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

}