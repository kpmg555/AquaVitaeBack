package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.ports.NotificacionPort;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class AlertaKafkaConsumer {

    @Inject
    NotificacionPort notificacionPort;

    @Incoming("alertas-in")
    @Blocking
    public void process(AlertaKafkaMessage msg) {
        System.out.println("[AlertaKafka] Procesando alerta para planta=" + msg.plantaId()
                + " tipo=" + msg.alerta().getTipo());
        notificacionPort.enviarNotificacionRiesgo(msg.alerta(), msg.plantaId());
    }
}