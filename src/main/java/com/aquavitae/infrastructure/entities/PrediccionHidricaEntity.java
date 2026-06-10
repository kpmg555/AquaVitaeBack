package com.aquavitae.infrastructure.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Predicciones del índice hídrico generadas por el modelo ARIMA (batch).
 * Mapea la tabla Prediccion_Hidrica que llena el microservicio Python.
 */
@Entity
@Table(name = "Prediccion_Hidrica")
public class PrediccionHidricaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_planta", nullable = false)
    private Integer idPlanta;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "horizonte_dia", nullable = false)
    private Integer horizonteDia;

    @Column(name = "valor_predicho", nullable = false)
    private Double valorPredicho;

    @Column(name = "banda_inferior")
    private Double bandaInferior;

    @Column(name = "banda_superior")
    private Double bandaSuperior;

    @Column(name = "modelo", length = 40)
    private String modelo;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo;

    public Integer getId() { return id; }
    public Integer getIdPlanta() { return idPlanta; }
    public LocalDate getFecha() { return fecha; }
    public Integer getHorizonteDia() { return horizonteDia; }
    public Double getValorPredicho() { return valorPredicho; }
    public Double getBandaInferior() { return bandaInferior; }
    public Double getBandaSuperior() { return bandaSuperior; }
    public String getModelo() { return modelo; }
    public LocalDateTime getFechaCalculo() { return fechaCalculo; }
}
