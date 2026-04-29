package com.aquavitae.application.dto;

import java.time.LocalDateTime;

public class AlertaDto {
    private final Integer id;
    private final String tipo;
    private final String titulo;
    private final String descripcion;
    private final float  nivelActual;
    private final LocalDateTime fecha;

    public AlertaDto(Integer id, String tipo, String titulo,
                     String descripcion, float nivelActual, LocalDateTime fecha) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nivelActual = nivelActual;
        this.fecha = fecha;
    }

    public Integer getId() { return id; }
    public String getTipo() { return tipo; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public float getNivelActual() { return nivelActual; }
    public LocalDateTime getFecha() { return fecha; }
}