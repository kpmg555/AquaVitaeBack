package com.aquavitae.application.dto;

import java.math.BigDecimal;

public class AlternativaUbicacionDto {
    public Integer plantaId;
    public String nombre;
    public String estado;
    public String riesgo;
    public String riesgoLabel;
    public BigDecimal costoCierre;
    public BigDecimal costoApertura;
    public BigDecimal costoTotal;
    public int tiempoCierreDias;
    public int tiempoAperturaMin;
    public int tiempoAperturaMax;
    public boolean recomendada;
}
