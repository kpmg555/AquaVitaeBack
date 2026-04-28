// Representa una lectura del estado hídrico de una planta.
// Corresponde a la tabla Estado_Planta de la BD.
// NO es una entidad JPA

package com.aquavitae.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class EstadoPlanta {
    private UUID id;
    private Integer idPlanta;
    private LocalDateTime fechaRegistro;
    private Float nivelAgua;
    private Float indiceHidrico;
    private Float umbral;
    private Boolean eventoExtremo;
    private String fuente;
    private String tipoDato;
    private String unidadNivel;

    public EstadoPlanta() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getIdPlanta() { return idPlanta; }
    public void setIdPlanta(Integer idPlanta) { this.idPlanta = idPlanta; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Float getNivelAgua() { return nivelAgua; }
    public void setNivelAgua(Float nivelAgua) { this.nivelAgua = nivelAgua; }

    public Float getIndiceHidrico() { return indiceHidrico; }
    public void setIndiceHidrico(Float indiceHidrico) { this.indiceHidrico = indiceHidrico; }

    public Float getUmbral() { return umbral; }
    public void setUmbral(Float umbral) { this.umbral = umbral; }

    public Boolean getEventoExtremo() { return eventoExtremo; }
    public void setEventoExtremo(Boolean eventoExtremo) { this.eventoExtremo = eventoExtremo; }

    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }

    public String getTipoDato() { return tipoDato; }
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }

    public String getUnidadNivel() { return unidadNivel; }
    public void setUnidadNivel(String unidadNivel) { this.unidadNivel = unidadNivel; }
}
