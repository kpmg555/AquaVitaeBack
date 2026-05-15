package com.aquavitae.infrastructure.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import com.aquavitae.domain.models.DatosClimaticos;
import com.aquavitae.domain.models.UbicacionClima;
import com.aquavitae.domain.ports.FuenteClimaPort;
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

    private List<Municipio> cacheMunicipios;
    private LocalDateTime lastFetch;
    private static final int CACHE_MINUTES = 15; // la API se actualiza cada 1h 15min

    @Override
    public List<DatosClimaticos> obtenerDatos(List<UbicacionClima> ubicaciones) {
        if (ubicaciones.isEmpty()) return List.of();

        // Carga o refresca el catálogo completo de municipios (solo si es necesario)
        cargarCache();

        List<DatosClimaticos> resultado = new ArrayList<>();

        for (UbicacionClima ub : ubicaciones) {
            Optional<Municipio> municipioOpt = buscarMunicipioMasCercano(
                    ub.getLatitud(), ub.getLongitud());

            if (municipioOpt.isPresent()) {
                Municipio m = municipioOpt.get();
                Double precipitacion = m.getPrecipitacion();
                Double temperatura = (m.getTemperaturaMax() + m.getTemperaturaMin()) / 2.0;
                Double humedadSuelo = m.getHumedadRelativa() / 100.0; // normalizado 0-1

                DatosClimaticos datos = new DatosClimaticos(
                        ub.getId(),
                        precipitacion,
                        humedadSuelo / 3,    // capa 0-1 cm
                        humedadSuelo / 3,    // capa 1-3 cm
                        humedadSuelo / 3,    // capa 3-9 cm
                        0.0,     // evapotranspiración no disponible
                        temperatura
                );
                resultado.add(datos);
            } else {
                // Municipio no encontrado: datos en ceros
                resultado.add(new DatosClimaticos(
                        ub.getId(), 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
            }
        }
        return resultado;
    }

    @Override
    public String getNombre() {
        return "smn";
    }

    private void cargarCache() {
        if (cacheMunicipios != null && lastFetch != null
                && ChronoUnit.MINUTES.between(lastFetch, LocalDateTime.now()) < CACHE_MINUTES) {
            return;
        }
        try {
            cacheMunicipios = smnRestClient.getPronosticoPorMunicipios(1);
            lastFetch = LocalDateTime.now();
        } catch (Exception e) {
            e.printStackTrace();
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
        // umbral ~50 km
        if (mejorDist > 0.5) {
            return Optional.empty();
        }
        return Optional.ofNullable(mejor);
    }

    private Double distanciaHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        Double dLat = Math.toRadians(lat2 - lat1);
        Double dLon = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); // radianes
    }
}