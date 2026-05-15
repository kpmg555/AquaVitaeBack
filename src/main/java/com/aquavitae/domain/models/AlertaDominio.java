package com.aquavitae.domain.models;

import java.time.LocalDateTime;

public class AlertaDominio {
    private Integer id;
    private Integer idPlanta;
    private String tipo;
    private String titulo;
    private String descripcion;
    private Double nivelActual;
    private Double umbral;
    private LocalDateTime fecha;

    public AlertaDominio(Integer id, Integer idPlanta, String tipo, String titulo,
                         String descripcion, Double nivelActual,
                         Double umbral, LocalDateTime fecha) {
        this.id = id;
        this.idPlanta = idPlanta;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nivelActual = nivelActual;
        this.umbral = umbral;
        this.fecha = fecha;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getIdPlanta() { return idPlanta; }
    public void setIdPlanta(Integer idPlanta) { this.idPlanta = idPlanta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getNivelActual() { return nivelActual; }
    public void setNivelActual(Double nivelActual) { this.nivelActual = nivelActual; }

    public Double getUmbral() { return umbral; }
    public void setUmbral(Double umbral) { this.umbral = umbral; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}