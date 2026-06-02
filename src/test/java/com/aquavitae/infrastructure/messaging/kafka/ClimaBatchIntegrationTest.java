package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.UbicacionClima;
import com.aquavitae.infrastructure.api.ClimaRestClient;
import com.aquavitae.infrastructure.api.ClimaticResponse;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ClimaBatchIntegrationTest {

    @Inject
    @Channel("clima-batch-out")
    Emitter<ClimaBatchMessage> emitter;

    @InjectMock
    @RestClient
    ClimaRestClient climaRestClient;

    @BeforeEach
    void setUp() {
        reset(climaRestClient);
    }

    @Test
    void enviarMensaje_debeLlamarAClimaRestClientConParametrosCorrectos() {
        UbicacionClima ubicacion = new UbicacionClima(1, 0.0, 0.0, 0);
        ubicacion.setLatitud(19.4326);
        ubicacion.setLongitud(-99.1332);
        ClimaBatchMessage mensaje = new ClimaBatchMessage("test-batch", ubicacion);

        ClimaticResponse response = mock(ClimaticResponse.class);
        ClimaticResponse.Current currentMock = mock(ClimaticResponse.Current.class);
        ClimaticResponse.Hourly hourlyMock = mock(ClimaticResponse.Hourly.class);

        when(response.getCurrent()).thenReturn(currentMock);
        when(response.getHourly()).thenReturn(hourlyMock);
        when(currentMock.getRain()).thenReturn(0.0);
        when(currentMock.getEvapotranspiration()).thenReturn(2.0);
        when(currentMock.getTemperature2m()).thenReturn(22.0);
        when(hourlyMock.getSoilMoisture0To1cm()).thenReturn(new Double[]{ 0.1 });
        when(hourlyMock.getSoilMoisture1To3cm()).thenReturn(new Double[]{ 0.2 });
        when(hourlyMock.getSoilMoisture3To9cm()).thenReturn(new Double[]{ 0.3 });

        when(climaRestClient.getForecast(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(response));

        emitter.send(mensaje);

        await().atMost(20, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(climaRestClient, times(1))
                        .getForecast(
                                eq("19.4326"),
                                eq("-99.1332"),
                                eq("temperature_2m,rain,evapotranspiration"),
                                eq("soil_moisture_0_to_1cm,soil_moisture_1_to_3cm,soil_moisture_3_to_9cm"),
                                eq("auto")));
    }

    @Test
    void enviarMensaje_siClienteRESTLanzaExcepcion_consumidorNoFalla() {
        UbicacionClima ubicacion = new UbicacionClima(1, 0.0, 0.0, 0);
        ubicacion.setLatitud(20.0);
        ubicacion.setLongitud(-100.0);
        ClimaBatchMessage mensaje = new ClimaBatchMessage("error-batch", ubicacion);

        when(climaRestClient.getForecast(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Error de red"));

        emitter.send(mensaje);

        await().atMost(20, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(climaRestClient, atLeastOnce())
                        .getForecast(any(), any(), any(), any(), any()));
    }
}
