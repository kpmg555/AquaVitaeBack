package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.RecuperacionDto;
import com.aquavitae.domain.models.EscenarioRecuperacion;
import com.aquavitae.domain.models.EscenarioRecuperacion.PuntoRecuperacion;
import com.aquavitae.domain.repository.SimulacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// SCRUM-228 · Pruebas unitarias back — Dashboard Simulación (Recuperación)
@ExtendWith(MockitoExtension.class)
class ObtenerRecuperacionUseCaseTest {

    @Mock
    SimulacionRepository simulacionRepository;

    @InjectMocks
    ObtenerRecuperacionUseCase useCase;

    private final int plantaId = 3;
    private final int dias = 90;
    private EscenarioRecuperacion escenario;

    @BeforeEach
    void setUp() {
        escenario = new EscenarioRecuperacion(
                List.of(new PuntoRecuperacion(1, 0.40f), new PuntoRecuperacion(2, 0.55f)),
                List.of(new PuntoRecuperacion(1, 0.40f), new PuntoRecuperacion(2, 0.38f))
        );
    }

    @Test
    void execute_happyPath_mapsBothScenarios() {
        // Arrange
        when(simulacionRepository.obtenerRecuperacion(plantaId, dias)).thenReturn(escenario);

        // Act
        RecuperacionDto dto = useCase.execute(plantaId, dias);

        // Assert
        assertNotNull(dto);
        assertEquals(2, dto.getConIntervencion().size());
        assertEquals(0.55f, dto.getConIntervencion().get(1).getValor(), 0.0001);
        assertEquals(2, dto.getSinIntervencion().size());
        assertEquals(0.38f, dto.getSinIntervencion().get(1).getValor(), 0.0001);
        assertEquals(1, dto.getConIntervencion().get(0).getDia());

        verify(simulacionRepository, times(1)).obtenerRecuperacion(plantaId, dias);
    }

    @Test
    void execute_emptyScenario_returnsEmptyLists() {
        // Arrange
        when(simulacionRepository.obtenerRecuperacion(anyInt(), anyInt()))
                .thenReturn(new EscenarioRecuperacion(List.of(), List.of()));

        // Act
        RecuperacionDto dto = useCase.execute(plantaId, dias);

        // Assert
        assertTrue(dto.getConIntervencion().isEmpty());
        assertTrue(dto.getSinIntervencion().isEmpty());
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        when(simulacionRepository.obtenerRecuperacion(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Falla"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.execute(plantaId, dias));
    }
}
