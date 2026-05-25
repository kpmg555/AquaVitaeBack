package com.aquavitae.infrastructure.messaging.kafka;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class AlertaKafkaMessageDeserializer extends JsonbDeserializer<AlertaKafkaMessage> {
    public AlertaKafkaMessageDeserializer() {
        super(AlertaKafkaMessage.class);
    }
}