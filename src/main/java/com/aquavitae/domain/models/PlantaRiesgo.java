package com.aquavitae.domain.models;

public class PlantaRiesgo {
    private Integer id;
    private String nombrePlanta;
    private Double latitud;
    private Double longitud;
    private Double indiceHidrico;
    private String nivelRiesgo;
    private String ubicacionNombre;      

    public PlantaRiesgo(Integer id, String nombrePlanta,
                        Double latitud, Double longitud,
                        Double indiceHidrico, String nivelRiesgo,
                        String ubicacionNombre) {
        this.id = id;
        this.nombrePlanta = nombrePlanta;
        this.latitud = latitud;
        this.longitud = longitud;
        this.indiceHidrico = indiceHidrico;
        this.nivelRiesgo = nivelRiesgo;
        this.ubicacionNombre = ubicacionNombre;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombrePlanta() { return nombrePlanta; }
    public void setNombrePlanta(String nombrePlanta) { this.nombrePlanta = nombrePlanta; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public Double getIndiceHidrico() { return indiceHidrico; }
    public void setIndiceHidrico(Double indiceHidrico) { this.indiceHidrico = indiceHidrico; }

    public String getNivelRiesgo() { return nivelRiesgo; }
    public void setNivelRiesgo(String nivelRiesgo) { this.nivelRiesgo = nivelRiesgo; }

    public String getUbicacionNombre() { return ubicacionNombre; }
    public void setUbicacionNombre(String ubicacionNombre) { this.ubicacionNombre = ubicacionNombre; }
}