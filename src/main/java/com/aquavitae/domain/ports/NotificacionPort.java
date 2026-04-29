package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.AlertaDominio;

public interface NotificacionPort {
    void enviarNotificacionRiesgo(AlertaDominio alerta, int plantaId);
}
