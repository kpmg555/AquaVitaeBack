package com.aquavitae.domain.models;

import java.time.LocalDateTime;

public class AlertaDominio {
    private Integer id;
    private String tipo;
    private String titulo;
    private String descripcion;
    private float nivelActual;
    private float umbral;
    private LocalDateTime fecha;

    public AlertaDominio(Integer id, String tipo, String titulo,
                         String descripcion, float nivelActual,
                         float umbral, LocalDateTime fecha) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nivelActual = nivelActual;
        this.umbral = umbral;
        this.fecha = fecha;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public float getNivelActual() { return nivelActual; }
    public void setNivelActual(float nivelActual) { this.nivelActual = nivelActual; }

    public float getUmbral() { return umbral; }
    public void setUmbral(float umbral) { this.umbral = umbral; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}