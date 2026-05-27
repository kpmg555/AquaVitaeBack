package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.AlertaDominio;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaKafkaProducerTest {

    @Mock
    Emitter<AlertaKafkaMessage> emitter;

    @InjectMocks
    AlertaKafkaProducer producer;

    @Test
    void publicarAlerta_debeEnviarMensajeAEmitter() {
        // Arrange
        AlertaDominio alerta = new AlertaDominio(1, 100, "RIESGO", "Test",
                "Desc", 0.2, 0.5, LocalDateTime.now());
        Integer plantaId = 42;

        // Act
        producer.publicarAlerta(alerta, plantaId);

        // Assert
        ArgumentCaptor<AlertaKafkaMessage> captor = ArgumentCaptor.forClass(AlertaKafkaMessage.class);
        verify(emitter).send(captor.capture());
        AlertaKafkaMessage mensajeEnviado = captor.getValue();

        assertEquals(plantaId, mensajeEnviado.plantaId());
        assertEquals(alerta, mensajeEnviado.alerta());
    }
}