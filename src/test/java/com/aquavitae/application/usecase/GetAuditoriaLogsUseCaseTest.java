package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.AuditoriaLog;
import com.aquavitae.domain.ports.AuditoriaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para GetAuditoriaLogsUseCase.
 *
 * Este caso de uso obtiene los registros recientes de auditoría aplicando
 * filtros opcionales como usuario, acción, módulo y severidad.
 *
 * Se mockea AuditoriaRepositoryPort para validar que los parámetros lleguen
 * correctamente al repositorio sin depender de base de datos.
 */
class GetAuditoriaLogsUseCaseTest {

    private AuditoriaRepositoryPort auditoriaRepositoryPort;
    private GetAuditoriaLogsUseCase getAuditoriaLogsUseCase;

    @BeforeEach
    void setUp() {
        auditoriaRepositoryPort = mock(AuditoriaRepositoryPort.class);

        getAuditoriaLogsUseCase = new GetAuditoriaLogsUseCase();
        getAuditoriaLogsUseCase.auditoriaRepositoryPort = auditoriaRepositoryPort;
    }

    /**
     * Verifica que el caso de uso retorne los logs encontrados por el repositorio.
     */
    @Test
    void execute_shouldReturnRecentLogs() {
        AuditoriaLog log = new AuditoriaLog(
                1,
                10,
                "Carlos",
                "CREATE",
                "USUARIOS",
                "Usuario",
                "Usuario creado correctamente",
                "127.0.0.1",
                "INFO",
                "{}",
                "{\"nombre\":\"Carlos\"}",
                LocalDateTime.of(2026, 6, 1, 10, 30)
        );

        when(auditoriaRepositoryPort.findRecentLogs(
                10,
                "Carlos",
                "CREATE",
                "USUARIOS",
                "INFO"
        )).thenReturn(List.of(log));

        List<AuditoriaLog> result = getAuditoriaLogsUseCase.execute(
                10,
                "Carlos",
                "CREATE",
                "USUARIOS",
                "INFO"
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Carlos", result.get(0).getUsuario());
        assertEquals("CREATE", result.get(0).getAccion());
        assertEquals("USUARIOS", result.get(0).getModulo());
        assertEquals("INFO", result.get(0).getSeveridad());

        verify(auditoriaRepositoryPort, times(1)).findRecentLogs(
                10,
                "Carlos",
                "CREATE",
                "USUARIOS",
                "INFO"
        );
    }

    /**
     * Verifica que el caso de uso pueda devolver una lista vacía.
     */
    @Test
    void execute_shouldReturnEmptyListWhenThereAreNoLogs() {
        when(auditoriaRepositoryPort.findRecentLogs(
                20,
                null,
                null,
                null,
                null
        )).thenReturn(List.of());

        List<AuditoriaLog> result = getAuditoriaLogsUseCase.execute(
                20,
                null,
                null,
                null,
                null
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(auditoriaRepositoryPort, times(1)).findRecentLogs(
                20,
                null,
                null,
                null,
                null
        );
    }
}