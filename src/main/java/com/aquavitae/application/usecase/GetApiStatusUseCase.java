package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.ApiMonitorStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetApiStatusUseCase {

    public List<ApiMonitorStatus> execute() {
        return List.of(
                new ApiMonitorStatus("Open-Meteo", "/forecast", "OK", 200, "API funcionando correctamente", 0),
                new ApiMonitorStatus("NASA POWER", "/daily/point", "OK", 200, "API funcionando correctamente", 0),
                new ApiMonitorStatus("SMN", "/pronostico", "ERROR", 404, "Endpoint no encontrado", 2)
        );
    }
}