package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.AuditoriaLog;
import com.aquavitae.domain.ports.AuditoriaRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetAuditoriaLogsUseCase {

    @Inject
    AuditoriaRepositoryPort auditoriaRepositoryPort;

    public List<AuditoriaLog> execute(
            Integer limit,
            String usuario,
            String accion,
            String modulo,
            String severidad
    ) {
        return auditoriaRepositoryPort.findRecentLogs(
                limit,
                usuario,
                accion,
                modulo,
                severidad
        );
    }
}