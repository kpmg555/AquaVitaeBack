package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.AlternativaUbicacionDto;
import com.aquavitae.domain.models.AlternativaUbicacion;
import com.aquavitae.domain.repository.AlternativasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ObtenerAlternativasUseCase {

    @Inject
    AlternativasRepository repository;

    public List<AlternativaUbicacionDto> execute(Integer plantaId) {
        return repository.obtenerAlternativas(plantaId).stream()
            .map(this::toDto)
            .toList();
    }

    private AlternativaUbicacionDto toDto(AlternativaUbicacion a) {
        AlternativaUbicacionDto dto = new AlternativaUbicacionDto();
        dto.plantaId         = a.getPlantaId();
        dto.nombre           = a.getNombre();
        dto.estado           = a.getEstado();
        dto.riesgo           = a.getRiesgo();
        dto.riesgoLabel      = a.getRiesgoLabel();
        dto.costoCierre      = a.getCostoCierre();
        dto.costoApertura    = a.getCostoApertura();
        dto.costoTotal       = a.getCostoTotal();
        dto.tiempoCierreDias = a.getTiempoCierreDias();
        dto.tiempoAperturaMin = a.getTiempoAperturaMin();
        dto.tiempoAperturaMax = a.getTiempoAperturaMax();
        dto.recomendada      = a.isRecomendada();
        return dto;
    }
}
