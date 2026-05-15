package com.aquavitae.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "Alerta")
public class AlertaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_planta", nullable = false)
    private Integer idPlanta;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "nivel_actual")
    private Double nivelActual;

    private Double umbral;

    @Column(length = 100)
    private String tendencia;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public AlertaEntity() {}

    public Integer getId() {return id;}
    public void setId(Integer id) {this.id = id;}

    public Integer getIdPlanta() {return idPlanta;}
    public void setIdPlanta(Integer idPlanta) {this.idPlanta = idPlanta;}

    public String getTipo() {return tipo;}
    public void setTipo(String tipo) {this.tipo = tipo;}

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public Double getNivelActual() {return nivelActual;}
    public void setNivelActual(Double nivelActual) {this.nivelActual = nivelActual;}

    public Double getUmbral() {return umbral;}
    public void setUmbral(Double umbral) {this.umbral = umbral;}

    public String getTendencia() {return tendencia;}
    public void setTendencia(String tendencia) {this.tendencia = tendencia;}

    public LocalDateTime getFecha() {return fecha;}
    public void setFecha(LocalDateTime fecha) {this.fecha = fecha;}
}
