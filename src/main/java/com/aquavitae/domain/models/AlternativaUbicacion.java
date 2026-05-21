package com.aquavitae.domain.models;

import java.math.BigDecimal;

public class AlternativaUbicacion {
    private Integer plantaId;
    private String nombre;
    private String estado;
    private String riesgo;       // "bajo", "medio", "alto"
    private String riesgoLabel;
    private BigDecimal costoCierre;
    private BigDecimal costoApertura;
    private BigDecimal costoTotal;
    private int tiempoCierreDias;
    private int tiempoAperturaMin;
    private int tiempoAperturaMax;
    private boolean recomendada;

    public AlternativaUbicacion(Integer plantaId, String nombre, String estado,
                                String riesgo, String riesgoLabel,
                                BigDecimal costoCierre, BigDecimal costoApertura, BigDecimal costoTotal,
                                int tiempoCierreDias, int tiempoAperturaMin, int tiempoAperturaMax,
                                boolean recomendada) {
        this.plantaId = plantaId;
        this.nombre = nombre;
        this.estado = estado;
        this.riesgo = riesgo;
        this.riesgoLabel = riesgoLabel;
        this.costoCierre = costoCierre;
        this.costoApertura = costoApertura;
        this.costoTotal = costoTotal;
        this.tiempoCierreDias = tiempoCierreDias;
        this.tiempoAperturaMin = tiempoAperturaMin;
        this.tiempoAperturaMax = tiempoAperturaMax;
        this.recomendada = recomendada;
    }

    public Integer getPlantaId() { return plantaId; }
    public String getNombre() { return nombre; }
    public String getEstado() { return estado; }
    public String getRiesgo() { return riesgo; }
    public String getRiesgoLabel() { return riesgoLabel; }
    public BigDecimal getCostoCierre() { return costoCierre; }
    public BigDecimal getCostoApertura() { return costoApertura; }
    public BigDecimal getCostoTotal() { return costoTotal; }
    public int getTiempoCierreDias() { return tiempoCierreDias; }
    public int getTiempoAperturaMin() { return tiempoAperturaMin; }
    public int getTiempoAperturaMax() { return tiempoAperturaMax; }
    public boolean isRecomendada() { return recomendada; }
}
