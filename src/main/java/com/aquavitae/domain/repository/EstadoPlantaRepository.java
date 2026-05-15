package com.aquavitae.domain.repository;

import java.time.LocalDateTime;

public interface EstadoPlantaRepository {
    void guardarEstado(int plantaId, Double indiceHidrico, LocalDateTime fecha);
}