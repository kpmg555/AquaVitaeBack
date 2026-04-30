package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.AlertaDto;
import com.aquavitae.domain.repository.AlertaRepository;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ObtenerAlertasUseCase {

    @Inject
    AlertaRepository alertaRepository;

    public List<AlertaDto> execute(int limit) {
        return alertaRepository.findRecientes(limit).stream()
                .map(a -> new AlertaDto(
                        a.getId(),
                        a.getTipo(),
                        a.getTitulo(),
                        a.getDescripcion(),
                        a.getNivelActual(),
                        a.getFecha()))
                .collect(Collectors.toList());
    }
}