package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.ApiMonitorStatus;
import com.aquavitae.domain.ports.ApiMonitorRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetApiStatusUseCase {

    @Inject
    ApiMonitorRepositoryPort repository;

    public List<ApiMonitorStatus> execute() {
        return repository.findStatus();
    }
}