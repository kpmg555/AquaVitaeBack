// Patron observer  para notificar a los usuarios sobre alertas de calidad del agua
// Notifica a todos los canales configurados sobre una nueva alerta hídrica.

package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.Alerta;

public interface NotificationService {
    void notificar(Alerta alerta);
}
