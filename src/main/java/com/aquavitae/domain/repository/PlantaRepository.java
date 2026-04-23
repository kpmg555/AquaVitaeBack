package com.aquavitae.domain.repository;

// INTERFAZ que el dominio exige para obtener datos.
//   El dominio define QUÉ necesita. La infraestructura define CÓMO lo obtiene.

import com.aquavitae.domain.models.PlantaRiesgo;
import java.util.List;

public interface PlantaRepository {
    List<PlantaRiesgo> findAllConRiesgo();
}
