package com.aquavitae.infrastructure.entities;

//   La representación JPA de la tabla Ubicacion en la BD.
//   Solo existe para que Hibernate pueda hacer el mapeo
//   objeto-relacional. No tiene lógica de negocio.
//El dominio no debe saber que existe JPA.

//Mapea la tabla Estado_Planta

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "nivel_agua", nullable = false)
    private Float nivelAgua;

    @Column(name = "indice_hidrico", nullable = false)
    private Float indiceHidrico;

    @Column(name = "evento_extremo")
    private Boolean eventoExtremo;

    public EstadoPlantaEntity() {}

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public Integer getIdPlanta() {return idPlanta;}
    public void setIdPlanta(Integer idPlanta) {this.idPlanta = idPlanta;}

    public LocalDateTime getFechaRegistro() {return fechaRegistro;}
    public void setFechaRegistro(LocalDateTime fechaRegistro) {this.fechaRegistro = fechaRegistro;}

    public Float getNivelAgua() {return nivelAgua;}
    public void setNivelAgua(Float nivelAgua) {this.nivelAgua = nivelAgua;}

    public Float getIndiceHidrico() {return indiceHidrico;}
    public void setIndiceHidrico(Float indiceHidrico) {this.indiceHidrico = indiceHidrico;}

    public Boolean getEventoExtremo() {return eventoExtremo;}
    public void setEventoExtremo(Boolean eventoExtremo) {this.eventoExtremo = eventoExtremo;}

}
