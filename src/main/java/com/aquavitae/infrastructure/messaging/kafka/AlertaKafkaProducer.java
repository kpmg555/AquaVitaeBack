package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.AlertaDominio;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class AlertaKafkaProducer {

    @Channel("alertas-out")
    Emitter<AlertaKafkaMessage> emitter;

    public void publicarAlerta(AlertaDominio alerta, Integer plantaId) {
        emitter.send(new AlertaKafkaMessage(plantaId, alerta));
    }
}