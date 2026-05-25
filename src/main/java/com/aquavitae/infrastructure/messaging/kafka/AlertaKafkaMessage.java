package com.aquavitae.infrastructure.messaging.kafka;

import com.aquavitae.domain.models.AlertaDominio;

public record AlertaKafkaMessage(Integer plantaId, AlertaDominio alerta) {
}