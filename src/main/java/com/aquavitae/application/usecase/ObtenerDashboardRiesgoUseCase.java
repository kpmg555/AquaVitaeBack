package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.DashboardRiesgoDto;
import com.aquavitae.application.dto.DashboardRiesgoDto.*;
import com.aquavitae.domain.models.DashboardRiesgo;
import com.aquavitae.domain.repository.DashboardRepository;
import java.util.stream.Collectors;

@ApplicationScoped
public class ObtenerDashboardRiesgoUseCase {

    @Inject
    DashboardRepository dashboardRepository;

    public DashboardRiesgoDto execute() {
        DashboardRiesgo dashboard = dashboardRepository.obtenerDashboard();

        return new DashboardRiesgoDto.Builder()
                .plantas(
                        dashboard.getPlantas().stream()
                                .map(p -> new PlantaRiesgoDto(
                                        p.getId(),
                                        p.getNombrePlanta(),
                                        p.getLatitud(),
                                        p.getLongitud(),
                                        p.getIndiceHidrico(),
                                        p.getNivelRiesgo()))
                                .collect(Collectors.toList()))
                .resumen(new ResumenDto(
                        dashboard.getResumen().getAlto(),
                        dashboard.getResumen().getMedio(),
                        dashboard.getResumen().getBajo()))
                .build();
    }
}