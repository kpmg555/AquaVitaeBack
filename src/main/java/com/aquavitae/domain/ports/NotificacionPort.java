package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.AlertaDominio;

import java.util.UUID;

public interface NotificacionPort {
    void enviarNotificacionRiesgo(AlertaDominio alerta, UUID plantaId);
}
