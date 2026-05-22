package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.FactorEvaluacionDto;
import com.aquavitae.domain.models.FactorEvaluacion;
import com.aquavitae.domain.repository.AlternativasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ObtenerFactoresUseCase {

    @Inject
    AlternativasRepository repository;

    public List<FactorEvaluacionDto> execute(Integer plantaId) {
        return repository.obtenerFactores(plantaId).stream()
            .map(this::toDto)
            .toList();
    }

    private FactorEvaluacionDto toDto(FactorEvaluacion f) {
        FactorEvaluacionDto dto = new FactorEvaluacionDto();
        dto.nombre = f.getNombre();
        dto.icono  = f.getIcono();
        dto.puntos = f.getPuntos();
        dto.color  = f.getColor();
        return dto;
    }
}
