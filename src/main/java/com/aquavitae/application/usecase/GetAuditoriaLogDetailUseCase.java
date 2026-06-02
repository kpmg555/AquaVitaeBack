package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.AuditoriaLog;
import com.aquavitae.domain.ports.AuditoriaRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class GetAuditoriaLogDetailUseCase {

    @Inject
    AuditoriaRepositoryPort auditoriaRepositoryPort;

    public AuditoriaLog execute(Integer id) {
        return auditoriaRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit log not found"));
    }
}