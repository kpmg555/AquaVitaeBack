package com.aquavitae.domain.repository;
import com.aquavitae.domain.models.PlantaRiesgo;
import java.util.List;

public interface PlantaRepository {

    // Devuelve todas las plantas activas con su último índice hídrico
    // y el índice de hace 7 días (para tendencia)
     List<PlantaRiesgo> findAllActivasConRiesgo();

}
