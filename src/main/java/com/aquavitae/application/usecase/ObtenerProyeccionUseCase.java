package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.ProyeccionDto;
import com.aquavitae.domain.models.ProyeccionHidrica;
import com.aquavitae.domain.repository.SimulacionRepository;

import java.util.stream.Collectors;

@ApplicationScoped
public class ObtenerProyeccionUseCase {

    @Inject
    SimulacionRepository simulacionRepository;

    public ProyeccionDto execute(Integer plantaId, int dias) {
        ProyeccionHidrica proyeccion = simulacionRepository.obtenerProyeccion(plantaId, dias);

        return new ProyeccionDto(
                proyeccion.getPuntos().stream()
                        .map(p -> new ProyeccionDto.PuntoDto(p.getDia(), p.getValor()))
                        .collect(Collectors.toList()),
                proyeccion.getBandaSuperior().stream()
                        .map(p -> new ProyeccionDto.PuntoDto(p.getDia(), p.getValor()))
                        .collect(Collectors.toList()),
                proyeccion.getBandaInferior().stream()
                        .map(p -> new ProyeccionDto.PuntoDto(p.getDia(), p.getValor()))
                        .collect(Collectors.toList()),
                proyeccion.getStartDay(),
                proyeccion.getPeakDay(),
                proyeccion.getPeakValue()
        );
    }
}
