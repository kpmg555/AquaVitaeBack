package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.EvolucionRiesgoDto;
import com.aquavitae.domain.repository.EvolucionRepository;
import java.util.stream.Collectors;

@ApplicationScoped
public class ObtenerEvolucionUseCase {

    @Inject
    EvolucionRepository evolucionRepository;

    public EvolucionRiesgoDto execute(int dias) {
        var evolucion = evolucionRepository.obtenerEvolucion(dias);
        var puntos = evolucion.getPuntos().stream()
                .map(p -> new EvolucionRiesgoDto.PuntoDto(p.getFecha(), p.getValorPromedio()))
                .collect(Collectors.toList());
        return new EvolucionRiesgoDto(puntos);
    }
}