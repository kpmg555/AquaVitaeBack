package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.EvolucionHidrica;

public interface EvolucionRepository {
    EvolucionHidrica obtenerEvolucion(int dias);
}
