package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.KpiSimulacionDto;
import com.aquavitae.domain.models.KpiSimulacion;
import com.aquavitae.domain.repository.SimulacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// SCRUM-228 · Pruebas unitarias back — Dashboard Simulación (KPIs)
// Prueba el caso de uso aislado, mockeando el repositorio (sin BD ni Quarkus).
@ExtendWith(MockitoExtension.class)
class ObtenerKpisSimulacionUseCaseTest {

    @Mock
    SimulacionRepository simulacionRepository;

    @InjectMocks
    ObtenerKpisSimulacionUseCase useCase;

    private final int plantaId = 5;
    private KpiSimulacion kpis;

    @BeforeEach
    void setUp() {
        kpis = new KpiSimulacion(0.75f, 12, 0.40f, 150000.0);
    }

    @Test
    void execute_happyPath_returnsKpiDto() {
        // Arrange
        when(simulacionRepository.obtenerKpis(plantaId)).thenReturn(kpis);

        // Act
        KpiSimulacionDto dto = useCase.execute(plantaId);

        // Assert: el DTO refleja exactamente los valores del dominio
        assertNotNull(dto);
        assertEquals(0.75f, dto.getIndiceHidricoActual(), 0.0001);
        assertEquals(12, dto.getDiasHastaUmbralCritico());
        assertEquals(0.40f, dto.getProbabilidadEventoCritico(), 0.0001);
        assertEquals(150000.0, dto.getPerdidaEconomicaProyectada(), 0.0001);

        verify(simulacionRepository, times(1)).obtenerKpis(plantaId);
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        when(simulacionRepository.obtenerKpis(plantaId))
                .thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> useCase.execute(plantaId));
        assertEquals("DB error", thrown.getMessage());
        verify(simulacionRepository).obtenerKpis(plantaId);
    }
}
