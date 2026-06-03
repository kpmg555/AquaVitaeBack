package com.aquavitae.infrastructure.api;

import com.aquavitae.domain.models.DatosClimaticos;
import com.aquavitae.domain.models.UbicacionClima;
import com.aquavitae.domain.ports.ApiMonitorRepositoryPort;
import com.aquavitae.domain.ports.FuenteClimaPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Named("nasaPower")
public class NasaPowerClimaAdapter implements FuenteClimaPort {

    @Inject
    @RestClient
    NasaPowerRestClient nasaPowerRestClient;

    @Inject
    ApiMonitorRepositoryPort apiMonitorRepository;

    private static final String PARAMETERS = "T2M,PRECTOTCORR,RH2M";
    private static final String COMMUNITY = "AG";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String API_NAME = "NASA POWER";
    private static final String API_BASE_URL = "https://power.larc.nasa.gov";
    private static final String API_ENDPOINT = "/api/temporal/daily/point";

    @Override
    public String getNombre() {
        return "nasaPower";
    }

    @Override
    public List<DatosClimaticos> obtenerDatos(List<UbicacionClima> ubicaciones) {
        if (ubicaciones.isEmpty()) return List.of();

        LocalDate hoy = LocalDate.now();
        String fechaInicio = hoy.format(DATE_FORMAT);
        String fechaFin = hoy.format(DATE_FORMAT);

        List<DatosClimaticos> resultado = new ArrayList<>();

        for (UbicacionClima ub : ubicaciones) {
            try {
                NasaPowerResponse response = nasaPowerRestClient.getDailyData(
                        PARAMETERS,
                        COMMUNITY,
                        ub.getLongitud(),
                        ub.getLatitud(),
                        fechaInicio,
                        fechaFin,
                        "JSON"
                );

                apiMonitorRepository.registerSuccess(
                        API_NAME,
                        API_BASE_URL,
                        "GET",
                        API_ENDPOINT
                );

                Double temperatura = response.getParameterValue("T2M") != null
                        ? response.getParameterValue("T2M").doubleValue()
                        : 0.0;

                Double precipitacion = response.getParameterValue("PRECTOTCORR") != null
                        ? response.getParameterValue("PRECTOTCORR").doubleValue()
                        : 0.0;

                Double humedadRelativa = response.getParameterValue("RH2M") != null
                        ? response.getParameterValue("RH2M").doubleValue()
                        : 0.0;

                Double humedadSuelo = humedadRelativa / 100.0;

                DatosClimaticos datos = new DatosClimaticos(
                        ub.getId(),
                        precipitacion,
                        humedadSuelo / 3,
                        humedadSuelo / 3,
                        humedadSuelo / 3,
                        0.0,
                        temperatura
                );

                resultado.add(datos);

            } catch (WebApplicationException e) {
                int statusCode = e.getResponse() != null
                        ? e.getResponse().getStatus()
                        : 500;

                apiMonitorRepository.registerError(
                        API_NAME,
                        API_BASE_URL,
                        "GET",
                        API_ENDPOINT,
                        statusCode,
                        e.getMessage()
                );

                throw e;

            } catch (Exception e) {
                apiMonitorRepository.registerError(
                        API_NAME,
                        API_BASE_URL,
                        "GET",
                        API_ENDPOINT,
                        500,
                        e.getMessage()
                );

                throw e;
            }
        }

        return resultado;
    }
}