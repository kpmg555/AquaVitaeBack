package com.aquavitae.domain.models;

// Contiene reglas de negocio : "una planta con su estado de riesgo hídrico actual"
//  No depende de ninguna capa externa

public class PlantaRiesgo {
    private Integer idPlanta;
    private String nombrePlanta;
    private String nombreUbicacion;
    private Float latitud;
    private Float longitud;
    private Float indiceHidrico;
    private Boolean tieneAlertaActiva;

    public PlantaRiesgo() {}

    public PlantaRiesgo(Integer idPlanta, String nombrePlanta,
                        String nombreUbicacion, Float latitud,
                        Float longitud, Float indiceHidrico,
                        Boolean tieneAlertaActiva) {
        this.idPlanta = idPlanta;
        this.nombrePlanta = nombrePlanta;
        this.nombreUbicacion = nombreUbicacion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.indiceHidrico= indiceHidrico;
        this.tieneAlertaActiva = tieneAlertaActiva;
    }

    // Logica de negocio para calcular el nivel de riesgo hídrico basado en el índice hídrico
    public String calcularNivelRiesgo() {
        if (indiceHidrico == null) return "DESCONOCIDO";
        if (indiceHidrico < 0.30) return "ALTO";
        if (indiceHidrico < 0.60) return "MEDIO";
        return "BAJO";
    }

    public String calcularColorSemaforo() {
        return switch (calcularNivelRiesgo()) {
            case "ALTO"  -> "rojo";
            case "MEDIO" -> "amarillo";
            case "BAJO"  -> "verde";
            default  -> "gris";
        };
    }

    //getters y setters
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
    public Boolean getTieneAlertaActiva()  { return tieneAlertaActiva; }
    public void setTieneAlertaActiva(Boolean v){ this.tieneAlertaActiva = v; }

}
