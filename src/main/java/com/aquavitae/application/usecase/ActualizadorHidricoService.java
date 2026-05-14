package com.aquavitae.application.usecase;

import io.quarkus.arc.All;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
        // 1. Seleccionar la fuente de clima (Strategy)
        FuenteClimaPort fuente = fuentes.stream()
                .filter(f -> fuenteActiva.equals(f.getNombre()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró fuente de clima: " + fuenteActiva));

        // 2. Obtener ubicaciones de plantas activas
        List<UbicacionClima> ubicaciones = plantaRepository.obtenerUbicacionesActivas();

        // 3. Obtener datos climáticos
        List<DatosClimaticos> datosList = fuente.obtenerDatos(ubicaciones);

        // 4. Procesar cada planta
        for (int i = 0; i < datosList.size(); i++) {
            DatosClimaticos datos = datosList.get(i);
            int plantaId = ubicaciones.get(i).getId();

            double indiceDouble = CalcularIndiceHidrico.calcular(datos);
            float indice = (float) indiceDouble;

            estadoPlantaRepository.guardarEstado(plantaId, indice, LocalDateTime.now());

            if (indice >= 0.75f) {
                AlertaDominio alerta = new AlertaDominio(
                        null, plantaId, "CRÍTICO",
                        "Índice hídrico crítico en planta " + plantaId,
                        String.format("Índice de estrés %.2f supera umbral 0.75", indice),
                        indice,
                        0.75f,
                        LocalDateTime.now()
                );
                alertaRepository.save(alerta);
                notificacionPort.enviarNotificacionRiesgo(alerta, plantaId);
            }
        }
    }
}