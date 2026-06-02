package com.aquavitae.application.usecase;

import com.aquavitae.domain.ports.AuditoriaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para GetAuditoriaResumenUseCase.
 *
 * Este caso de uso obtiene los indicadores principales de auditoría:
 * eventos del día, cambios críticos, usuarios auditados y registros inmutables.
 *
 * Se mockea AuditoriaRepositoryPort para evitar dependencia de base de datos
 * y probar únicamente la lógica del caso de uso.
 */
class GetAuditoriaResumenUseCaseTest {

    private AuditoriaRepositoryPort auditoriaRepositoryPort;
    private GetAuditoriaResumenUseCase getAuditoriaResumenUseCase;

    /**
     * Configura los mocks antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        auditoriaRepositoryPort = mock(AuditoriaRepositoryPort.class);

        getAuditoriaResumenUseCase = new GetAuditoriaResumenUseCase();
        getAuditoriaResumenUseCase.auditoriaRepositoryPort = auditoriaRepositoryPort;
    }

    /**
     * Verifica que el caso de uso construya correctamente el resumen
     * con los conteos obtenidos desde el repositorio.
     */
    @Test
    void execute_shouldReturnAuditoriaResumenWithCorrectCounts() {
        when(auditoriaRepositoryPort.countLogsToday()).thenReturn(12);
        when(auditoriaRepositoryPort.countCriticalChanges()).thenReturn(3);
        when(auditoriaRepositoryPort.countAuditedUsers()).thenReturn(5);
        when(auditoriaRepositoryPort.countImmutableLogs()).thenReturn(40);

        Map<String, Integer> result = getAuditoriaResumenUseCase.execute();

        assertNotNull(result);
        assertEquals(12, result.get("eventosHoy"));
        assertEquals(3, result.get("cambiosCriticos"));
        assertEquals(5, result.get("usuariosAuditados"));
        assertEquals(40, result.get("registrosInmutables"));

        verify(auditoriaRepositoryPort, times(1)).countLogsToday();
        verify(auditoriaRepositoryPort, times(1)).countCriticalChanges();
        verify(auditoriaRepositoryPort, times(1)).countAuditedUsers();
        verify(auditoriaRepositoryPort, times(1)).countImmutableLogs();
    }
}