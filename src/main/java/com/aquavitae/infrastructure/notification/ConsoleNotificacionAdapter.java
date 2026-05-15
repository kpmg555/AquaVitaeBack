package com.aquavitae.infrastructure.notification;

import jakarta.enterprise.context.ApplicationScoped;
import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.ports.NotificacionPort;

@ApplicationScoped
public class ConsoleNotificacionAdapter implements NotificacionPort {

    @Override
    public void enviarNotificacionRiesgo(AlertaDominio alerta, int plantaId) {
        System.out.println("NOTIFICACIÓN (simulada): " + alerta.getTitulo() +
                " para planta " + plantaId);
    }
}