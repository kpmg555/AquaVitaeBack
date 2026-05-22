package com.aquavitae.infrastructure.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import com.aquavitae.domain.models.DatosClimaticos;
import com.aquavitae.domain.models.UbicacionClima;
import com.aquavitae.domain.ports.FuenteClimaPort;
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

    @Override
    public String getNombre() { return "openMeteo"; }

    @Override
    public List<DatosClimaticos> obtenerDatos(List<UbicacionClima> ubicaciones) {
        if (ubicaciones.isEmpty()) return List.of();

        String latStr = ubicaciones.stream()
                .map(u -> String.valueOf(u.getLatitud()))
                .collect(Collectors.joining(","));
        String lonStr = ubicaciones.stream()
                .map(u -> String.valueOf(u.getLongitud()))
                .collect(Collectors.joining(","));

        String current = "temperature_2m,rain,evapotranspiration";
        String hourly = "soil_moisture_0_to_1cm,soil_moisture_1_to_3cm,soil_moisture_3_to_9cm";

        List<ClimaticResponse> responses = climaRestClient.getForecast(
                latStr, lonStr, current, hourly, "auto");

        List<DatosClimaticos> resultado = new ArrayList<>();

        for (int i = 0; i < ubicaciones.size(); i++) {
            UbicacionClima ub = ubicaciones.get(i);
            if (i >= responses.size()) break;
            ClimaticResponse response = responses.get(i);

            double rain = 0.0, temp = 0.0, evap = 0.0;
            double ultH0_1 = 0.0, ultH1_3 = 0.0, ultH3_9 = 0.0;

            if (response.getCurrent() != null) {
                rain = response.getCurrent().getRain() != null ? response.getCurrent().getRain() : 0.0;
                temp = response.getCurrent().getTemperature2m() != null ? response.getCurrent().getTemperature2m() : 0.0;
                evap = response.getCurrent().getEvapotranspiration() != null ? response.getCurrent().getEvapotranspiration() : 0.0;
            }
            if (response.getHourly() != null) {
                Double[] hum0_1 = response.getHourly().getSoilMoisture0To1cm();
                Double[] hum1_3 = response.getHourly().getSoilMoisture1To3cm();
                Double[] hum3_9 = response.getHourly().getSoilMoisture3To9cm();
                ultH0_1 = (hum0_1 != null && hum0_1.length > 0 && hum0_1[hum0_1.length - 1] != null) ? hum0_1[hum0_1.length - 1] : 0.0;
                ultH1_3 = (hum1_3 != null && hum1_3.length > 0 && hum1_3[hum1_3.length - 1] != null) ? hum1_3[hum1_3.length - 1] : 0.0;
                ultH3_9 = (hum3_9 != null && hum3_9.length > 0 && hum3_9[hum3_9.length - 1] != null) ? hum3_9[hum3_9.length - 1] : 0.0;
            }

            resultado.add(new DatosClimaticos(ub.getId(), rain, ultH0_1, ultH1_3, ultH3_9, evap, temp));
        }
        return resultado;
    }

}