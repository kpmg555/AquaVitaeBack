//   Usar strategy porque  la clasificación de riesgo
//   es una regla que puede cambiar
//   Sin Strategy: cambiar la regla implica modificar PlantaRiesgo
//   o el usecase
//   Con Strategy: se crea una nueva implementación de la interfaz
//   y se inyecta. Las clases existentes no se tocan.
//   Es lógica de negocio. No depende de ningún  framework ni de la BD.

package com.aquavitae.domain.service;
import com.aquavitae.domain.models.NivelRiesgo;

public interface ClasificadorRiesgoStrategy {
    NivelRiesgo clasificar(Float indiceHidrico);
}