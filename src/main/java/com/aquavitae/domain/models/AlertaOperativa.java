package com.aquavitae.domain.models;

import java.math.BigDecimal;

public class AlertaOperativa {
    private float indiceActual;
    private int diasCierreRecomendado;
    private BigDecimal costoApertura;
    private BigDecimal costoOperacionEstimada;
    private int diasReaperturaMin;
    private int diasReaperturaMax;
    private String nombrePlanta;

    public AlertaOperativa(float indiceActual, int diasCierreRecomendado,
                           BigDecimal costoApertura, BigDecimal costoOperacionEstimada,
                           int diasReaperturaMin, int diasReaperturaMax, String nombrePlanta) {
        this.indiceActual = indiceActual;
        this.diasCierreRecomendado = diasCierreRecomendado;
        this.costoApertura = costoApertura;
        this.costoOperacionEstimada = costoOperacionEstimada;
        this.diasReaperturaMin = diasReaperturaMin;
        this.diasReaperturaMax = diasReaperturaMax;
        this.nombrePlanta = nombrePlanta;
    }

    public float getIndiceActual() { return indiceActual; }
    public int getDiasCierreRecomendado() { return diasCierreRecomendado; }
    public BigDecimal getCostoApertura() { return costoApertura; }
    public BigDecimal getCostoOperacionEstimada() { return costoOperacionEstimada; }
    public int getDiasReaperturaMin() { return diasReaperturaMin; }
    public int getDiasReaperturaMax() { return diasReaperturaMax; }
    public String getNombrePlanta() { return nombrePlanta; }
}
