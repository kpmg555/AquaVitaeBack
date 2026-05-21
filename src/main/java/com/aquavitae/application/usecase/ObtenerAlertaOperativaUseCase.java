package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.AlertaOperativaDto;
import com.aquavitae.domain.models.AlertaOperativa;
import com.aquavitae.domain.repository.AlternativasRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ObtenerAlertaOperativaUseCase {

    @Inject
    AlternativasRepository repository;

    public AlertaOperativaDto execute(Integer plantaId) {
        AlertaOperativa alerta = repository.obtenerAlerta(plantaId);
        AlertaOperativaDto dto = new AlertaOperativaDto();
        dto.nombrePlanta          = alerta.getNombrePlanta();
        dto.indiceActual          = alerta.getIndiceActual();
        dto.diasCierreRecomendado = alerta.getDiasCierreRecomendado();
        dto.costoApertura         = alerta.getCostoApertura();
        dto.costoOperacionEstimada = alerta.getCostoOperacionEstimada();
        dto.diasReaperturaMin     = alerta.getDiasReaperturaMin();
        dto.diasReaperturaMax     = alerta.getDiasReaperturaMax();
        return dto;
    }
}
