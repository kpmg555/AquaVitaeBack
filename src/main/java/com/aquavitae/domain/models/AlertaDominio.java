package com.aquavitae.domain.models;

import java.time.LocalDateTime;

public class AlertaDominio {
    private Integer id;
    private String tipo;
    private String titulo;
    private String descripcion;
    private float nivelActual;
    private LocalDateTime fecha;

    public AlertaDominio(Integer id, String tipo, String titulo,
                         String descripcion, float nivelActual, LocalDateTime fecha) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nivelActual = nivelActual;
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

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}