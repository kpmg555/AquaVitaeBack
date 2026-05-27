package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.ports.NotificacionPort;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@QuarkusTest
class AlertaKafkaIntegrationTest {

    @Inject
    @Channel("alertas-out")
    Emitter<AlertaKafkaMessage> emitter;

    @InjectMock
    NotificacionPort notificacionPort;

    @BeforeEach
    void setUp() {
        reset(notificacionPort);
    }

    @Test
    void enviarMensaje_debeSerConsumidoYLlamarNotificacionPort() {
        AlertaDominio alerta = new AlertaDominio(1, 100, "RIESGO", "Integracion",
                "Test", 0.4, 0.7, LocalDateTime.now());
        AlertaKafkaMessage mensaje = new AlertaKafkaMessage(55, alerta);

        emitter.send(mensaje);

        await().atMost(60, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(notificacionPort, times(1))
                        .enviarNotificacionRiesgo(any(AlertaDominio.class), eq(55)));
    }

    @Test
    void enviarMultiplesMensajes_seProcesanTodos() {
        for (int i = 1; i <= 3; i++) {
            AlertaDominio alerta = new AlertaDominio(i, 100, "INFO", "Msg" + i,
                    "", 0.1, 0.5, LocalDateTime.now());
            emitter.send(new AlertaKafkaMessage(10, alerta));
        }

        await().atMost(60, TimeUnit.SECONDS)
                .pollInterval(1, TimeUnit.SECONDS)
                .untilAsserted(() -> verify(notificacionPort, times(3))
                        .enviarNotificacionRiesgo(any(AlertaDominio.class), anyInt()));
    }
}
