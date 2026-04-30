package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.UbicacionClima;
import java.util.List;

public interface PlantaRepository {
    List<UbicacionClima> obtenerUbicacionesActivas();
}