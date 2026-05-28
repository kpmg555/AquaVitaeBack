package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.AuditoriaLog;

import java.util.List;
import java.util.Optional;

public interface AuditoriaRepositoryPort {

    List<AuditoriaLog> findRecentLogs(
            Integer limit,
            String usuario,
            String accion,
            String modulo,
            String severidad
    );

    Optional<AuditoriaLog> findById(Integer id);

    int countLogsToday();

    int countCriticalChanges();

    int countAuditedUsers();

    int countImmutableLogs();
}