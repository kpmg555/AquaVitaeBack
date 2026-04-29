//Puerto de salida para consultas de plantas activas.
// Devuelve todas las plantas activas con sus coordenadas y parámetros necesarios para la ingesta.

package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.Planta;
import com.aquavitae.domain.models.UbicacionClima;

import java.util.List;

public interface PlantaQueryRepository {
    List<Planta> findAllActivas();
}

