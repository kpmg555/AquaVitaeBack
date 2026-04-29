// Puerto de salida para persistir los estados hídricos calculados por los servicios de ingesta.

package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.EstadoPlanta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public interface EstadoPlantaRepository {

    void saveAll(UUID plantaId, double indice, LocalDateTime now);
}