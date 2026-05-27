package com.aquavitae.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ClimaBatchMessageDeserializer
        extends ObjectMapperDeserializer<ClimaBatchMessage> {
    public ClimaBatchMessageDeserializer() {
        super(ClimaBatchMessage.class);
    }
}