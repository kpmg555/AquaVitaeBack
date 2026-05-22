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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Named("smn")
public class SmnClimaAdapter implements FuenteClimaPort {

    @Inject
    @RestClient
    SmnRestClient smnRestClient;

    @Inject
    ApiMonitorRepositoryPort apiMonitorRepository;

    private List<Municipio> cacheMunicipios;
    private LocalDateTime lastFetch;
    private static final int CACHE_MINUTES = 15;

    @Override
    public String getNombre() {
        return "smn";
    }

    @Override
    public List<DatosClimaticos> obtenerDatos(List<UbicacionClima> ubicaciones) {
        if (ubicaciones.isEmpty()) return List.of();

        cargarCache();

        List<DatosClimaticos> resultado = new ArrayList<>();

        for (UbicacionClima ub : ubicaciones) {
            Optional<Municipio> municipioOpt = buscarMunicipioMasCercano(
                    ub.getLatitud(),
                    ub.getLongitud()
            );

            if (municipioOpt.isPresent()) {
                Municipio m = municipioOpt.get();

                Double precipitacion = m.getPrecipitacion();
                Double temperatura = (m.getTemperaturaMax() + m.getTemperaturaMin()) / 2.0;
                Double humedadSuelo = m.getHumedadRelativa() / 100.0;

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
            } else {
                resultado.add(new DatosClimaticos(
                        ub.getId(),
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0,
                        0.0
                ));
            }
        }

        return resultado;
    }

    private void cargarCache() {
        if (cacheMunicipios != null && lastFetch != null
                && ChronoUnit.MINUTES.between(lastFetch, LocalDateTime.now()) < CACHE_MINUTES) {
            return;
        }

        try {
            cacheMunicipios = smnRestClient.getPronosticoPorMunicipios(1);
            lastFetch = LocalDateTime.now();

            apiMonitorRepository.registerSuccess(
                    "SMN",
                    "https://smn.conagua.gob.mx",
                    "GET",
                    "/webservices/?method=1"
            );

        } catch (WebApplicationException e) {
            int statusCode = e.getResponse() != null
                    ? e.getResponse().getStatus()
                    : 500;

            apiMonitorRepository.registerError(
                    "SMN",
                    "https://smn.conagua.gob.mx",
                    "GET",
                    "/webservices/?method=1",
                    statusCode,
                    e.getMessage()
            );

            cacheMunicipios = List.of();

        } catch (Exception e) {
            apiMonitorRepository.registerError(
                    "SMN",
                    "https://smn.conagua.gob.mx",
                    "GET",
                    "/webservices/?method=1",
                    500,
                    e.getMessage()
            );

            cacheMunicipios = List.of();
        }
    }

    private Optional<Municipio> buscarMunicipioMasCercano(Double lat, Double lon) {
        Municipio mejor = null;
        Double mejorDist = Double.MAX_VALUE;

        for (Municipio m : cacheMunicipios) {
            Double dist = distanciaHaversine(lat, lon, m.getLatitud(), m.getLongitud());

            if (dist < mejorDist) {
                mejorDist = dist;
                mejor = m;
            }
        }

        if (mejorDist > 0.5) {
            return Optional.empty();
        }

        return Optional.ofNullable(mejor);
    }

    private Double distanciaHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        Double dLat = Math.toRadians(lat2 - lat1);
        Double dLon = Math.toRadians(lon2 - lon1);

        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}