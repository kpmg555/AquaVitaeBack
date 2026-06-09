package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.AuditoriaLog;
import com.aquavitae.domain.ports.AuditoriaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para GetAuditoriaLogDetailUseCase.
 *
 * Este caso de uso obtiene el detalle de un registro específico de auditoría.
 * Si el registro no existe, lanza una excepción.
 */
class GetAuditoriaLogDetailUseCaseTest {

    private AuditoriaRepositoryPort auditoriaRepositoryPort;
    private GetAuditoriaLogDetailUseCase getAuditoriaLogDetailUseCase;

    @BeforeEach
    void setUp() {
        auditoriaRepositoryPort = mock(AuditoriaRepositoryPort.class);

        getAuditoriaLogDetailUseCase = new GetAuditoriaLogDetailUseCase();
        getAuditoriaLogDetailUseCase.auditoriaRepositoryPort = auditoriaRepositoryPort;
    }

    /**
     * Verifica que el caso de uso devuelva el detalle cuando el log existe.
     */
    @Test
    void execute_shouldReturnLogDetailWhenLogExists() {
        Integer logId = 1;

        AuditoriaLog log = new AuditoriaLog(
                1,
                10,
                "Carlos",
                "UPDATE",
                "APIS",
                "ApiKey",
                "Rotación de API key",
                "127.0.0.1",
                "WARNING",
                "{\"activa\":true}",
                "{\"activa\":true}",
                LocalDateTime.of(2026, 6, 1, 12, 0)
        );

        when(auditoriaRepositoryPort.findById(logId)).thenReturn(Optional.of(log));

        AuditoriaLog result = getAuditoriaLogDetailUseCase.execute(logId);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Carlos", result.getUsuario());
        assertEquals("UPDATE", result.getAccion());
        assertEquals("APIS", result.getModulo());
        assertEquals("WARNING", result.getSeveridad());
        assertTrue(result.isInmutable());
        assertNotNull(result.getHashIntegridad());

        verify(auditoriaRepositoryPort, times(1)).findById(logId);
    }

    /**
     * Verifica que el caso de uso lance excepción cuando el log no existe.
     */
    @Test
    void execute_shouldThrowRuntimeExceptionWhenLogDoesNotExist() {
        Integer logId = 999;

        when(auditoriaRepositoryPort.findById(logId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            getAuditoriaLogDetailUseCase.execute(logId);
        });

        assertEquals("Audit log not found", exception.getMessage());

        verify(auditoriaRepositoryPort, times(1)).findById(logId);
    }
}