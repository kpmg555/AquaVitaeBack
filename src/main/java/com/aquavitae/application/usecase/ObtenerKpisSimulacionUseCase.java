package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.KpiSimulacionDto;
import com.aquavitae.domain.models.KpiSimulacion;
import com.aquavitae.domain.repository.SimulacionRepository;

@ApplicationScoped
public class ObtenerKpisSimulacionUseCase {

    @Inject
    SimulacionRepository simulacionRepository;

    public KpiSimulacionDto execute(Integer plantaId) {
        KpiSimulacion kpis = simulacionRepository.obtenerKpis(plantaId);
        return new KpiSimulacionDto(
                kpis.getIndiceHidricoActual(),
                kpis.getDiasHastaUmbralCritico(),
                kpis.getProbabilidadEventoCritico(),
                kpis.getPerdidaEconomicaProyectada()
        );
    }
}
