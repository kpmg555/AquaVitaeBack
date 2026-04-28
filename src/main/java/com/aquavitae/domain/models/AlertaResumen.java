package com.aquavitae.domain.models;
import java.time.LocalDateTime;
import java.util.UUID;

public class AlertaResumen {
    private UUID id;
    private String tipo;       // 'CRÍTICO' | 'ADVERTENCIA' | 'INFORMATIVO'
    private String titulo;
    private String  descripcion;
    private LocalDateTime fecha;

    public AlertaResumen() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
