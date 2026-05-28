package com.aquavitae.application.usecase;

import com.aquavitae.domain.ports.AuditoriaRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Map;

@ApplicationScoped
public class GetAuditoriaResumenUseCase {

    @Inject
    AuditoriaRepositoryPort auditoriaRepositoryPort;

    public Map<String, Integer> execute() {
        return Map.of(
                "eventosHoy", auditoriaRepositoryPort.countLogsToday(),
                "cambiosCriticos", auditoriaRepositoryPort.countCriticalChanges(),
                "usuariosAuditados", auditoriaRepositoryPort.countAuditedUsers(),
                "registrosInmutables", auditoriaRepositoryPort.countImmutableLogs()
        );
    }
}