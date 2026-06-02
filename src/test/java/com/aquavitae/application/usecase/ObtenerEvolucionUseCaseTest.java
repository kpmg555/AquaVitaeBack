package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.EvolucionRiesgoDto;
import com.aquavitae.domain.models.EvolucionHidrica;
import com.aquavitae.domain.repository.EvolucionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerEvolucionUseCaseTest {

    @Mock
    EvolucionRepository evolucionRepository;

    @InjectMocks
    ObtenerEvolucionUseCase useCase;

    private EvolucionHidrica evolucion;

    @BeforeEach
    void setUp() {
        List<EvolucionHidrica.PuntoDiario> puntos = List.of(
                new EvolucionHidrica.PuntoDiario(LocalDate.of(2025, 1, 1), 0.65),
                new EvolucionHidrica.PuntoDiario(LocalDate.of(2025, 1, 2), 0.70));
        evolucion = new EvolucionHidrica(puntos);
    }

    @Test
    void execute_happyPath_returnsEvolucionDtoWithPoints() {
        // Arrange
        int dias = 30;
        when(evolucionRepository.obtenerEvolucion(dias)).thenReturn(evolucion);

        // Act
        EvolucionRiesgoDto result = useCase.execute(dias);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getPuntos().size());
        EvolucionRiesgoDto.PuntoDto punto1 = result.getPuntos().get(0);
        assertEquals(LocalDate.of(2025, 1, 1), punto1.getFecha());
        assertEquals(0.65, punto1.getValorPromedio());
        EvolucionRiesgoDto.PuntoDto punto2 = result.getPuntos().get(1);
        assertEquals(LocalDate.of(2025, 1, 2), punto2.getFecha());
        assertEquals(0.70, punto2.getValorPromedio());

        verify(evolucionRepository, times(1)).obtenerEvolucion(dias);
    }

    @Test
    void execute_emptyPointsList_returnsDtoWithEmptyPoints() {
        // Arrange
        int dias = 7;
        EvolucionHidrica emptyEvolucion = new EvolucionHidrica(List.of());
        when(evolucionRepository.obtenerEvolucion(dias)).thenReturn(emptyEvolucion);

        // Act
        EvolucionRiesgoDto result = useCase.execute(dias);

        // Assert
        assertNotNull(result);
        assertTrue(result.getPuntos().isEmpty());
        verify(evolucionRepository).obtenerEvolucion(dias);
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        int dias = 15;
        when(evolucionRepository.obtenerEvolucion(dias)).thenThrow(new IllegalArgumentException("Invalid days"));

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> useCase.execute(dias));
        assertEquals("Invalid days", thrown.getMessage());
        verify(evolucionRepository).obtenerEvolucion(dias);
    }
}