package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.UbicacionClima;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClimaBatchProducerTest {

    @Mock
    Emitter<ClimaBatchMessage> emitter;

    @InjectMocks
    ClimaBatchProducer producer;

    @Test
    void publishBatch_debeEnviarUnMensajePorUbicacion_ConMismoBatchId() {
        // Arrange
        UbicacionClima u1 = new UbicacionClima(1, 0.0, 0.0, 0);
        UbicacionClima u2 = new UbicacionClima(2, 0.0, 0.0, 0);
        u2.setId(2);
        List<UbicacionClima> ubicaciones = List.of(u1, u2);

        // Act
        String batchId = producer.publishBatch(ubicaciones);

        // Assert
        assertNotNull(batchId);
        ArgumentCaptor<ClimaBatchMessage> captor = ArgumentCaptor.forClass(ClimaBatchMessage.class);
        verify(emitter, times(2)).send(captor.capture());

        List<ClimaBatchMessage> mensajes = captor.getAllValues();
        assertEquals(2, mensajes.size());
        assertEquals(batchId, mensajes.get(0).batchId());
        assertEquals(batchId, mensajes.get(1).batchId());
        assertEquals(1, mensajes.get(0).ubicacion().getId());
        assertEquals(2, mensajes.get(1).ubicacion().getId());
    }
}