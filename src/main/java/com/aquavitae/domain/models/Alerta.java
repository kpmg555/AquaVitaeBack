// Objeto de dominio que representa una alerta hídrica.
// Corresponde a la tabla Alerta de la BD.

package com.aquavitae.domain.models;
import java.time.LocalDateTime;
import java.util.UUID;

public class Alerta {
    private UUID id;
    private UUID idPlanta;
    private String tipo;          // 'CRÍTICO' | 'ADVERTENCIA' | 'INFORMATIVO'
    private String titulo;
    private String descripcion;
    private Float nivelActual;
    private Float umbral;
    private String tendencia;
    private LocalDateTime fecha;

    public Alerta() {
    }

    public Alerta(UUID id, UUID idPlanta, String tipo, String titulo, String descripcion, Float nivelActual, Float umbral, String tendencia, LocalDateTime fecha) {
        this.id = id;
        this.idPlanta = idPlanta;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nivelActual = nivelActual;
        this.umbral = umbral;
        this.tendencia = tendencia;
        this.fecha = fecha;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getIdPlanta() { return idPlanta; }
    public void setIdPlanta(UUID idPlanta) { this.idPlanta = idPlanta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Float getNivelActual() { return nivelActual; }
    public void setNivelActual(Float nivelActual) { this.nivelActual = nivelActual;}

    public Float getUmbral() { return umbral; }
    public void setUmbral(Float umbral) { this.umbral = umbral; }

    public String getTendencia() { return tendencia; }
    public void setTendencia(String tendencia) { this.tendencia = tendencia; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
