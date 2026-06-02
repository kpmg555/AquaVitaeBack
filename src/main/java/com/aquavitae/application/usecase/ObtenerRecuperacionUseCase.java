package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.RecuperacionDto;
import com.aquavitae.domain.models.EscenarioRecuperacion;
import com.aquavitae.domain.repository.SimulacionRepository;

import java.util.stream.Collectors;

@ApplicationScoped
public class ObtenerRecuperacionUseCase {

    @Inject
    SimulacionRepository simulacionRepository;

    public RecuperacionDto execute(Integer plantaId, int dias) {
        EscenarioRecuperacion escenario = simulacionRepository.obtenerRecuperacion(plantaId, dias);

        return new RecuperacionDto(
                escenario.getConIntervencion().stream()
                        .map(p -> new RecuperacionDto.PuntoDto(p.getDia(), p.getValor()))
                        .collect(Collectors.toList()),
                escenario.getSinIntervencion().stream()
                        .map(p -> new RecuperacionDto.PuntoDto(p.getDia(), p.getValor()))
                        .collect(Collectors.toList())
        );
    }
}
