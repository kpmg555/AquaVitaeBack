package com.aquavitae.domain.service;

import com.aquavitae.domain.models.DatosClimaticos;

public class CalcularIndiceHidrico {

    public static double calcular(DatosClimaticos datos) {

        double factorLluvia = Math.min(1.0, datos.getPrecipitacionMm() / 50.0);
        double humedad = datos.getHumedadSuelo();
        double factorEvap = 1.0 - Math.min(1.0, datos.getEvapotranspiracion() / 5.0);
        double factorTemp = 1.0 - Math.min(1.0, datos.getTemperatura() / 40.0);

        return (factorLluvia * 0.3 + humedad * 0.4 + factorEvap * 0.2 + factorTemp * 0.1);
    }
}