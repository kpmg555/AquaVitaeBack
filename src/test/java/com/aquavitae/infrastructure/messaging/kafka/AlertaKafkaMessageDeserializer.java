package com.aquavitae.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class AlertaKafkaMessageDeserializer
        extends ObjectMapperDeserializer<AlertaKafkaMessage> {
    public AlertaKafkaMessageDeserializer() {
        super(AlertaKafkaMessage.class);
    }
}