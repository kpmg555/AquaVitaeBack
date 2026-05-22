package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.UbicacionClima;

// Envelope que viaja por Kafka. 
// Lleva BatchId para rastrear a qué corrida pertenece cada ubicación
public record ClimaBatchMessage(String batchId, UbicacionClima ubicacion) {
}
