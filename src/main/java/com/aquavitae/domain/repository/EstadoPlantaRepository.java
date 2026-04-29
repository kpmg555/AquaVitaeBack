// Puerto de salida para persistir los estados hídricos calculados por los servicios de ingesta.

package com.aquavitae.domain.repository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;


public interface EstadoPlantaRepository {

    void saveAll(UUID plantaId, double indice, LocalDateTime now);

    @Transactional
    void guardarEstado(int plantaId, double indiceHidrico, LocalDateTime fecha);
}