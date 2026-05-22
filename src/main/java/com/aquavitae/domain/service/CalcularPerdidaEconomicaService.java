package com.aquavitae.domain.service;

import java.math.BigDecimal;

public class CalcularPerdidaEconomicaService {

    public static double calcular(int diasHastaUmbral, BigDecimal costoOperacionDiaria) {
        if (diasHastaUmbral <= 0 || costoOperacionDiaria == null) return 0.0;
        return diasHastaUmbral * costoOperacionDiaria.doubleValue();
    }
}
