package com.aquavitae.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class ClimaBatchMessageDeserializer extends JsonbDeserializer<ClimaBatchMessage> {
    public ClimaBatchMessageDeserializer() {
        super(ClimaBatchMessage.class);
    }
}