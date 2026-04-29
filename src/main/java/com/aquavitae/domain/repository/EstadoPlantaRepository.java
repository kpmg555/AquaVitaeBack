package com.aquavitae.domain.repository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

public interface EstadoPlantaRepository {
    void guardarEstado(int plantaId, double indiceHidrico, LocalDateTime fecha);

    @Transactional
    void guardarEstado(int plantaId, float indiceHidrico, LocalDateTime fecha);
}