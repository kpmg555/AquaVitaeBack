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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Named("openMeteo")
public class OpenMeteoClimaAdapter implements FuenteClimaPort {

    @Inject
    @RestClient
    ClimaRestClient climaRestClient;

    @Inject
    ApiMonitorRepositoryPort apiMonitorRepository;

    private static final String API_NAME = "Open-Meteo";
    private static final String API_BASE_URL = "https://api.open-meteo.com";
    private static final String API_ENDPOINT = "/v1/forecast";
    private static final String CURRENT_PARAMS = "temperature_2m,rain,evapotranspiration";
    private static final String HOURLY_PARAMS = "soil_moisture_0_to_1cm,soil_moisture_1_to_3cm,soil_moisture_3_to_9cm";

    @Override
    public String getNombre() {
        return "openMeteo";
    }

    @Override
    public List<DatosClimaticos> obtenerDatos(List<UbicacionClima> ubicaciones) {
        if (ubicaciones.isEmpty()) return List.of();

        String latStr = ubicaciones.stream()
                .map(u -> String.valueOf(u.getLatitud()))
                .collect(Collectors.joining(","));

        String lonStr = ubicaciones.stream()
                .map(u -> String.valueOf(u.getLongitud()))
                .collect(Collectors.joining(","));

        List<ClimaticResponse> responses = callApi(latStr, lonStr);

        List<DatosClimaticos> resultado = new ArrayList<>();
        for (int i = 0; i < ubicaciones.size(); i++) {
            if (i >= responses.size()) break;
            resultado.add(buildDatosClimaticos(ubicaciones.get(i), responses.get(i)));
        }

        return resultado;
    }

    private List<ClimaticResponse> callApi(String latStr, String lonStr) {
        try {
            List<ClimaticResponse> responses = climaRestClient.getForecast(
                    latStr, lonStr, CURRENT_PARAMS, HOURLY_PARAMS, "auto");
            apiMonitorRepository.registerSuccess(API_NAME, API_BASE_URL, "GET", API_ENDPOINT);
            return responses;
        } catch (WebApplicationException e) {
            int statusCode = e.getResponse() != null ? e.getResponse().getStatus() : 500;
            apiMonitorRepository.registerError(API_NAME, API_BASE_URL, "GET", API_ENDPOINT, statusCode, e.getMessage());
            throw e;
        } catch (Exception e) {
            apiMonitorRepository.registerError(API_NAME, API_BASE_URL, "GET", API_ENDPOINT, 500, e.getMessage());
            throw e;
        }
    }

    private DatosClimaticos buildDatosClimaticos(UbicacionClima ub, ClimaticResponse response) {
        double rain = 0.0, temp = 0.0, evap = 0.0;
        double ultH0_1 = 0.0, ultH1_3 = 0.0, ultH3_9 = 0.0;

        if (response.getCurrent() != null) {
            rain = doubleOrZero(response.getCurrent().getRain());
            temp = doubleOrZero(response.getCurrent().getTemperature2m());
            evap = doubleOrZero(response.getCurrent().getEvapotranspiration());
        }

        if (response.getHourly() != null) {
            ultH0_1 = getLastValue(response.getHourly().getSoilMoisture0To1cm());
            ultH1_3 = getLastValue(response.getHourly().getSoilMoisture1To3cm());
            ultH3_9 = getLastValue(response.getHourly().getSoilMoisture3To9cm());
        }

        return new DatosClimaticos(ub.getId(), rain, ultH0_1, ultH1_3, ultH3_9, evap, temp);
    }

    private double getLastValue(Double[] arr) {
        return (arr != null && arr.length > 0 && arr[arr.length - 1] != null)
                ? arr[arr.length - 1]
                : 0.0;
    }

    private double doubleOrZero(Double value) {
        return value != null ? value : 0.0;
    }
}
