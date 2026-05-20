package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.ApiAlert;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetApiAlertsUseCase {

    public List<ApiAlert> execute() {
        return List.of(
                new ApiAlert("SMN", "/pronostico", 404, "Endpoint no encontrado", "ALTA"),
                new ApiAlert("NASA POWER", "/daily/point", 401, "Credencial inválida o expirada", "CRITICA")
        );
    }
}