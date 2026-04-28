// Puerto de salida para persistir los estados hídricos calculados por los servicios de ingesta.

package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.EstadoPlanta;
import java.util.List;


public interface EstadoPlantaRepository {

    void saveAll(List<EstadoPlanta> estados);
}