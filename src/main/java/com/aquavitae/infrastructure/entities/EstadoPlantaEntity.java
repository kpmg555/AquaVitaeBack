package com.aquavitae.infrastructure.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

import java.util.UUID;

@Entity
@Table(name = "Estado_Planta")
public class EstadoPlantaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @Column(name = "id_planta", nullable = false)
    private Integer idPlanta;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    private Float umbral;

    @Column(name = "proyeccion_nivel_mm")
    private Float proyeccionNivelMm;

    @Column(name = "nivel_agua", nullable = false)
    private Float nivelAgua;

    @Column(name = "indice_hidrico", nullable = false)
    private Float indiceHidrico;

    @Column(name = "evento_extremo")
    private Boolean eventoExtremo = false;

    @Column(name = "tipo_dato", length = 30)
    private String tipoDato = "historico";

    @Column(name = "unidad_nivel", length = 20)
    private String unidadNivel = "mm";

    @Column(length = 50)
    private String fuente = "OPEN_METEO";

    public EstadoPlantaEntity() {}

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public Integer getIdPlanta() {return idPlanta;}
    public void setIdPlanta(Integer idPlanta) {this.idPlanta = idPlanta;}

    public LocalDateTime getFechaRegistro() {return fechaRegistro;}
    public void setFechaRegistro(LocalDateTime fechaRegistro) {this.fechaRegistro = fechaRegistro;}

    public Float getUmbral() {return umbral;}
    public void setUmbral(Float umbral) {this.umbral = umbral;}

    public Float getProyeccionNivelMm() {return proyeccionNivelMm;}
    public void setProyeccionNivelMm(Float proyeccionNivelMm) {this.proyeccionNivelMm = proyeccionNivelMm;}

    public Float getNivelAgua() {return nivelAgua;}
    public void setNivelAgua(Float nivelAgua) {this.nivelAgua = nivelAgua;}

    public Float getIndiceHidrico() {return indiceHidrico;}
    public void setIndiceHidrico(Float indiceHidrico) {this.indiceHidrico = indiceHidrico;}

    public Boolean getEventoExtremo() {return eventoExtremo;}
    public void setEventoAlto(Boolean eventoExtremo) {this.eventoExtremo = eventoExtremo;}

    public String getTipoDato() {return tipoDato;}
    public void setTipoDato(String tipoDato) {this.tipoDato = tipoDato;}

    public String getUnidadNivel() {return unidadNivel;}
    public void setUnidadNivel(String unidadNivel) {this.unidadNivel = unidadNivel;}

    public String getFuente() {return fuente;}
    public void setFuente(String fuente) {this.fuente = fuente;}


}
