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
import com.aquavitae.domain.repository.PlantaRepository;
import com.aquavitae.domain.service.CalcularIndiceHidrico;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class ActualizadorHidricoService {

    @Inject
    @ConfigProperty(name = "fuente.clima.activa", defaultValue = "openMeteo")
    String fuenteActiva;

    @Inject
    @All
    List<FuenteClimaPort> fuentes;

    @Inject
    PlantaRepository plantaRepository;

    @Inject
    EstadoPlantaRepository estadoPlantaRepository;

    @Inject
    AlertaRepository alertaRepository;

    @Inject
    NotificacionPort notificacionPort;

    public void ejecutarActualizacion() {
        // Seleccionar estrategia
        FuenteClimaPort fuente = fuentes.stream()
                .filter(f -> f.getClass().getAnnotation(Named.class).value().equals(fuenteActiva))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Fuente de clima no encontrada: " + fuenteActiva));

        // Obtener ubicaciones de plantas activas
        List<UbicacionClima> ubicaciones = plantaRepository.obtenerUbicacionesActivas();

        // Obtener datos climáticos
        List<DatosClimaticos> datosList = fuente.obtenerDatos(ubicaciones);

        // Procesar cada planta
        for (int i = 0; i < datosList.size(); i++) {
            DatosClimaticos datos = datosList.get(i);
            int plantaId = ubicaciones.get(i).getId();

            float indice = (float) CalcularIndiceHidrico.calcular(datos);

            estadoPlantaRepository.guardarEstado(plantaId, indice, LocalDateTime.now());

            if (indice < 0.3) { // ALTO
                AlertaDominio alerta = new AlertaDominio(
                        null, "ALTO",
                        "Índice hídrico crítico en planta " + plantaId,
                        String.format("Índice %.2f bajo umbral 0.3", indice),
                        indice,
                        LocalDateTime.now()
                );
                alertaRepository.save(alerta);
                notificacionPort.enviarNotificacionRiesgo(alerta, plantaId);
            }
        }
    }
}