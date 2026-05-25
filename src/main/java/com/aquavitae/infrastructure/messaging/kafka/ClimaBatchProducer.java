package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.UbicacionClima;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClimaBatchProducer {

    @Channel("clima-batch-out")
    Emitter<ClimaBatchMessage> emitter;

    public String publishBatch(List<UbicacionClima> ubicaciones) {
        String batchId = UUID.randomUUID().toString();

        for (UbicacionClima u : ubicaciones) {
            emitter.send(new ClimaBatchMessage(batchId, u));
        }
        return batchId;
    }

}
