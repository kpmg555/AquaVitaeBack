package com.aquavitae.application.usecase;

import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import com.aquavitae.domain.models.*;
import com.aquavitae.domain.ports.FuenteClimaPort;
import com.aquavitae.domain.ports.NotificacionPort;
import com.aquavitae.domain.repository.AlertaRepository;
import com.aquavitae.domain.repository.EstadoPlantaRepository;
import com.aquavitae.domain.repository.PlantaQueryRepository;
import com.aquavitae.domain.service.CalcularIndiceHidrico;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ActualizadorHidricoService {

    @Inject
    @ConfigProperty(name = "fuente.clima.activa", defaultValue = "openMeteo")
    String fuenteActiva;

    @Inject
    @All
    List<FuenteClimaPort> fuentes;

    @Inject
    PlantaQueryRepository plantaRepository;

    @Inject
    EstadoPlantaRepository estadoPlantaRepository;

    @Inject
    AlertaRepository alertaRepository;

    @Inject
    NotificacionPort notificacionPort;

    public void ejecutarActualizacion() {
        // Seleccionar estrategia de clima
        FuenteClimaPort fuente = fuentes.stream()
                .filter(f -> f.getClass().getAnnotation(Named.class).value().equals(fuenteActiva))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró fuente de clima: " + fuenteActiva));

        // Obtener ubicaciones de plantas activas
        List<Planta> ubicaciones = plantaRepository.findAllActivas();

        //  Obtener datos climáticos
        List<DatosClimaticos> datosClimaticosList = fuente.obtenerDatos(ubicaciones);

        //  Calcular índice, guardar y alertar
        for (DatosClimaticos datos : datosClimaticosList) {
            float indice = (float) CalcularIndiceHidrico.calcular(datos);
            int index = datosClimaticosList.indexOf(datos);
            UUID plantaId = ubicaciones.get(index).getId();

            estadoPlantaRepository.saveAll(plantaId, indice, LocalDateTime.now());

            if (indice < 0.3) {
                AlertaDominio alerta = new AlertaDominio(
                        null, "ALTO",
                        "Índice hídrico alto en planta " + plantaId,
                        "El índice actual es " + String.format("%.2f", indice) + ", bajo el umbral 0.3",
                        indice,
                        LocalDateTime.now()
                );
                alertaRepository.save(alerta);
                notificacionPort.enviarNotificacionRiesgo(alerta, plantaId);
            }
        }
    }
}