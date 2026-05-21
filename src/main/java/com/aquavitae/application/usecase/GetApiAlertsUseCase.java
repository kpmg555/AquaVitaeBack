package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.ApiAlert;
import com.aquavitae.domain.ports.ApiMonitorRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetApiAlertsUseCase {

    @Inject
    ApiMonitorRepositoryPort repository;

    public List<ApiAlert> execute() {
        return repository.findActiveAlerts();
    }
}