// Sin Facade: el usecase inyecta 3 repositorios distintos y construye la lógica de agregación él mismo.
//   Con Facade: el usecase inyecta UNA sola dependencia  y delega la lógica de agregación a la Facade,
//   que a su vez inyecta los repositorios necesarios.


package com.aquavitae.application.usecase;
import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.models.PlantaRiesgo;
import java.util.List;
import java.util.Map;

public interface RiesgoHidricoFacadePort {
    List<PlantaRiesgo> obtenerPlantasConRiesgo();
    long contarCrisisActivas();
    List<AlertaDominio> obtenerAlertasRecientes(int limite);
    Map<String, Float> obtenerEvolucion7Dias();
}
