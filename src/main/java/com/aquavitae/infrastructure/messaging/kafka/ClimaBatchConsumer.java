package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.DatosClimaticos;
import com.aquavitae.domain.models.UbicacionClima;
import com.aquavitae.infrastructure.api.ClimaRestClient;
import com.aquavitae.infrastructure.api.ClimaticResponse;
import java.util.List;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ClimaBatchConsumer {

    @Inject
    @RestClient
    ClimaRestClient climaRestClient;

    @Incoming("clima-batch-in")
    @Blocking // necesario porque ClimaRestClient es una llamada HTTP bloqueante
    public void process(ClimaBatchMessage msg) {
        UbicacionClima ub = msg.ubicacion();
        String current = "temperature_2m,rain,evapotranspiration";
        String hourly = "soil_moisture_0_to_1cm,soil_moisture_1_to_3cm,soil_moisture_3_to_9cm";

        try {
            List<ClimaticResponse> responses = climaRestClient.getForecast(
                    String.valueOf(ub.getLatitud()),
                    String.valueOf(ub.getLongitud()),
                    current, hourly, "auto");
            if (responses == null || responses.isEmpty()) return;
            ClimaticResponse response = responses.get(0);

            if (response.getCurrent() == null || response.getHourly() == null)
                return;

            Double[] hum0_1 = response.getHourly().getSoilMoisture0To1cm();
            Double[] hum1_3 = response.getHourly().getSoilMoisture1To3cm();
            Double[] hum3_9 = response.getHourly().getSoilMoisture3To9cm();

            DatosClimaticos datos = new DatosClimaticos(
                    ub.getId(),
                    response.getCurrent().getRain(),
                    last(hum0_1),
                    last(hum1_3),
                    last(hum3_9),
                    response.getCurrent().getEvapotranspiration(),
                    response.getCurrent().getTemperature2m());

            System.out.println("[ClimaBatch] batch=" + msg.batchId()
                    + " ubicacion=" + ub.getId()
                    + " temp=" + datos.getTemperatura()
                    + " humedad=" + datos.getHumedadSuelo());

        } catch (Exception e) {
            System.err.println("[ClimaBatch] Error ubicacion=" + ub.getId() + ": " + e.getMessage());
        }
    }

    private Double last(Double[] arr) {
        return (arr != null && arr.length > 0) ? arr[arr.length - 1] : 0.0;
    }
}