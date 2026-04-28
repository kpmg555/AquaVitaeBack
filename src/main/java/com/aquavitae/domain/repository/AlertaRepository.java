package com.aquavitae.domain.repository;
import com.aquavitae.domain.models.AlertaResumen;
import com.aquavitae.domain.models.PlantaRiesgo;
import java.util.List;

public interface AlertaRepository {
    // Alertas críticas no archivadas (para el contador de crisis)
    long contarCriticasActivas();
    // Últimas N alertas para el panel de alertas recientes
    List<AlertaResumen> findRecientes(int limite);
}
