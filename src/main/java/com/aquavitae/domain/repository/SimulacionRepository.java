package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.EscenarioRecuperacion;
import com.aquavitae.domain.models.KpiSimulacion;
import com.aquavitae.domain.models.ProyeccionHidrica;

public interface SimulacionRepository {
    KpiSimulacion obtenerKpis(Integer plantaId);
    ProyeccionHidrica obtenerProyeccion(Integer plantaId, int dias);
    EscenarioRecuperacion obtenerRecuperacion(Integer plantaId, int dias);
}
