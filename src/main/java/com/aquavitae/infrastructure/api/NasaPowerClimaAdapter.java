package com.aquavitae.infrastructure.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import com.aquavitae.domain.models.DatosClimaticos;
import com.aquavitae.domain.models.UbicacionClima;
import com.aquavitae.domain.ports.FuenteClimaPort;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Named("nasaPower")
public class NasaPowerClimaAdapter implements FuenteClimaPort {

    @Inject
    @RestClient
    NasaPowerRestClient nasaPowerRestClient;
    private static final String PARAMETERS = "T2M,PRECTOTCORR,RH2M";
    private static final String COMMUNITY = "AG"; // Agroclimatology
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public String getNombre() { return "nasaPower"; }

    @Override
    public List<DatosClimaticos> obtenerDatos(List<UbicacionClima> ubicaciones) {
        if (ubicaciones.isEmpty()) return List.of();

        LocalDate hoy = LocalDate.now();
        String fechaInicio = hoy.format(DATE_FORMAT);
        String fechaFin = hoy.format(DATE_FORMAT);

        List<DatosClimaticos> resultado = new ArrayList<>();

        for (UbicacionClima ub : ubicaciones) {
            NasaPowerResponse response = nasaPowerRestClient.getDailyData(
                    PARAMETERS,
                    COMMUNITY,
                    ub.getLongitud(),
                    ub.getLatitud(),
                    fechaInicio,
                    fechaFin,
                    "JSON"
            );

            float temperatura = response.getParameterValue("T2M") != null
                    ? response.getParameterValue("T2M").floatValue() : 0f;
            float precipitacion = response.getParameterValue("PRECTOTCORR") != null
                    ? response.getParameterValue("PRECTOTCORR").floatValue() : 0f;
            float humedadRelativa = response.getParameterValue("RH2M") != null
                    ? response.getParameterValue("RH2M").floatValue() : 0f;
            float humedadSuelo = humedadRelativa / 100f;   // normalizar [0,1]

            DatosClimaticos datos = new DatosClimaticos(
                    ub.getId(),
                    precipitacion,
                    humedadSuelo / 3f,    // capa 0–1 cm
                    humedadSuelo / 3f,    // capa 1–3 cm
                    humedadSuelo / 3f,    // capa 3–9 cm
                    0f,                   // evapotranspiración no disponible
                    temperatura
            );
            resultado.add(datos);
        }

        return resultado;
    }
}