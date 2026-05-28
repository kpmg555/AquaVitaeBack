package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.ports.NotificacionPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumidor de mensajes de alerta desde Kafka.
 * Escucha el canal 'alertas-in' y llama al puerto de notificación.
 * Si ocurre una excepción, se registra pero NO se relanza para evitar
 * reintentos infinitos.
 */
@ApplicationScoped
public class AlertaKafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AlertaKafkaConsumer.class);

    @Inject
    NotificacionPort notificacionPort;

    @Incoming("alertas-in")
    public void process(AlertaKafkaMessage message) {
        AlertaDominio alerta = message.alerta();
        Integer plantaId = message.plantaId();

        LOG.info("Procesando alerta para planta={}, tipo={}", plantaId, alerta.getTipo());

        try {
            // Llamar al puerto de notificación (puede lanzar excepción)
            notificacionPort.enviarNotificacionRiesgo(alerta, plantaId);
        } catch (Exception e) {
            // Registrar el error pero no relanzar. Esto permite que se confirme el offset
            // y no se reintente el mensaje una y otra vez.
            LOG.error("Error al enviar notificación para alerta {} de planta {}: {}",
                    alerta.getId(), plantaId, e.getMessage(), e);
            // Opcional: enviar a una cola de dead letter
        }
    }
}