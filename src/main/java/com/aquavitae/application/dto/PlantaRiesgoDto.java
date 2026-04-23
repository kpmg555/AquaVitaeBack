package com.aquavitae.application.dto;

// DTO para representar la información de una planta de riesgo
//Contrato de salida hacia el front
//Solo tiene los campos que la pantalla necesita
// Nunca expone la entity ni el modelo de dominio directamente.
// No tienen lógica de negocio ni  tocan base de datos

public class PlantaRiesgoDto {
    private Integer idPlanta;
    private String nombrePlanta;
    private String nombreUbicacion;
    private Float latitud;
    private Float longitud;
    private Float indiceHidrico;
    private String nivelRiesgo;
    private String colorSemaforo;
    private Boolean tieneAlertaActiva;

    public PlantaRiesgoDto() {}

    public PlantaRiesgoDto(Integer idPlanta, String nombrePlanta,
                           String nombreUbicacion, Float latitud,
                           Float longitud, Float indiceHidrico,
                           String nivelRiesgo, String colorSemaforo,
                           Boolean tieneAlertaActiva) {
        this.idPlanta = idPlanta;
        this.nombrePlanta = nombrePlanta;
        this.nombreUbicacion = nombreUbicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.indiceHidrico= indiceHidrico;
        this.nivelRiesgo = nivelRiesgo;
        this.colorSemaforo= colorSemaforo;
        this.tieneAlertaActiva = tieneAlertaActiva;
    }

    public Integer getIdPlanta()  { return idPlanta; }
    public void setIdPlanta(Integer v) { this.idPlanta = v; }
    public String getNombrePlanta()  { return nombrePlanta; }
    public void setNombrePlanta(String v)  { this.nombrePlanta = v; }
    public String getNombreUbicacion() { return nombreUbicacion; }
    public void setNombreUbicacion(String v)  { this.nombreUbicacion = v; }
    public Float getLatitud()  { return latitud; }
    public void setLatitud(Float v)  { this.latitud = v; }
    public Float getLongitud() { return longitud; }
    public void setLongitud(Float v) { this.longitud = v; }
    public Float getIndiceHidrico() { return indiceHidrico; }
    public void setIndiceHidrico(Float v)  { this.indiceHidrico = v; }
    public String getNivelRiesgo()  { return nivelRiesgo; }
    public void setNivelRiesgo(String v) { this.nivelRiesgo = v; }
    public String getColorSemaforo() { return colorSemaforo; }
    public void setColorSemaforo(String v) { this.colorSemaforo = v; }
    public Boolean getTieneAlertaActiva()  { return tieneAlertaActiva; }
    public void setTieneAlertaActiva(Boolean v){ this.tieneAlertaActiva = v; }
}
