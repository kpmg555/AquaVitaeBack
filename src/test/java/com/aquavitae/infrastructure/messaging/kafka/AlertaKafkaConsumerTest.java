package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.ports.NotificacionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

/**
 * Prueba unitaria del consumidor de Kafka.
 * Usa Mockito para mockear el puerto de notificación.
 * No necesita Kafka real.
 */
@ExtendWith(MockitoExtension.class)
class AlertaKafkaConsumerTest {

    @Mock
    NotificacionPort notificacionPort;

    @InjectMocks
    AlertaKafkaConsumer consumer;

    @Test
    void process_debeLlamarNotificacionPortConLosDatosCorrectos() {
        // Arrange
        AlertaDominio alerta = new AlertaDominio(1, 100, "RIESGO", "Alerta",
                "Desc", 0.3, 0.6, LocalDateTime.now());
        AlertaKafkaMessage msg = new AlertaKafkaMessage(99, alerta);

        // Act
        consumer.process(msg);

        // Assert
        verify(notificacionPort, times(1))
                .enviarNotificacionRiesgo(alerta, 99);
    }

    @Test
    void process_siNotificacionPortLanzaExcepcion_noSeRelanza() {
        // Arrange
        AlertaDominio alerta = new AlertaDominio(1, 100, "RIESGO", "Alerta",
                "Desc", 0.3, 0.6, LocalDateTime.now());
        AlertaKafkaMessage msg = new AlertaKafkaMessage(99, alerta);

        // Mockear lanzamiento de excepción usando argument matchers correctos
        doThrow(new RuntimeException("Error en notificación"))
                .when(notificacionPort)
                .enviarNotificacionRiesgo(any(AlertaDominio.class), anyInt());

        // Act (no debe lanzar excepción)
        consumer.process(msg);

        // Assert: se llamó al puerto (aunque falló internamente)
        verify(notificacionPort).enviarNotificacionRiesgo(any(AlertaDominio.class), eq(99));
    }
}