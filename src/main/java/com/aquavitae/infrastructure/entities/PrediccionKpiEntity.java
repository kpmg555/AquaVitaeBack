package com.aquavitae.infrastructure.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * KPIs derivados del pronóstico ARIMA (tabla Prediccion_Kpi que llena el batch Python).
 * evento crítico = escasez: índice <= umbral.
 */
@Entity
@Table(name = "Prediccion_Kpi")
public class PrediccionKpiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_planta", nullable = false)
    private Integer idPlanta;

    @Column(name = "indice_actual")
    private Float indiceActual;

    @Column(name = "dias_hasta_umbral")
    private Integer diasHastaUmbral;

    @Column(name = "probabilidad_evento")
    private Float probabilidadEvento;

    @Column(name = "perdida_estimada")
    private Double perdidaEstimada;

    @Column(name = "umbral")
    private Float umbral;

    @Column(name = "modelo", length = 40)
    private String modelo;

    @Column(name = "fecha_calculo")
    private LocalDateTime fechaCalculo;

    public Integer getId() { return id; }
    public Integer getIdPlanta() { return idPlanta; }
    public Float getIndiceActual() { return indiceActual; }
    public Integer getDiasHastaUmbral() { return diasHastaUmbral; }
    public Float getProbabilidadEvento() { return probabilidadEvento; }
    public Double getPerdidaEstimada() { return perdidaEstimada; }
    public Float getUmbral() { return umbral; }
    public String getModelo() { return modelo; }
    public LocalDateTime getFechaCalculo() { return fechaCalculo; }
}
