package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.ProyeccionDto;
import com.aquavitae.domain.models.ProyeccionHidrica;
import com.aquavitae.domain.models.ProyeccionHidrica.PuntoProyeccion;
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

// SCRUM-228 · Pruebas unitarias back — Dashboard Simulación (Proyección)
@ExtendWith(MockitoExtension.class)
class ObtenerProyeccionUseCaseTest {

    @Mock
    SimulacionRepository simulacionRepository;

    @InjectMocks
    ObtenerProyeccionUseCase useCase;

    private final int plantaId = 5;
    private final int dias = 90;
    private ProyeccionHidrica proyeccion;

    @BeforeEach
    void setUp() {
        proyeccion = new ProyeccionHidrica(
                List.of(new PuntoProyeccion(1, 0.50f), new PuntoProyeccion(2, 0.60f)),
                List.of(new PuntoProyeccion(1, 0.55f), new PuntoProyeccion(2, 0.65f)),
                List.of(new PuntoProyeccion(1, 0.45f), new PuntoProyeccion(2, 0.55f)),
                1, 2, 0.60f
        );
    }

    @Test
    void execute_happyPath_mapsPointsAndBands() {
        // Arrange
        when(simulacionRepository.obtenerProyeccion(plantaId, dias)).thenReturn(proyeccion);

        // Act
        ProyeccionDto dto = useCase.execute(plantaId, dias);

        // Assert
        assertNotNull(dto);
        assertEquals(2, dto.getPuntos().size());
        assertEquals(1, dto.getPuntos().get(0).getDia());
        assertEquals(0.50f, dto.getPuntos().get(0).getValor(), 0.0001);
        assertEquals(2, dto.getBandaSuperior().size());
        assertEquals(0.65f, dto.getBandaSuperior().get(1).getValor(), 0.0001);
        assertEquals(2, dto.getBandaInferior().size());
        assertEquals(0.45f, dto.getBandaInferior().get(0).getValor(), 0.0001);
        assertEquals(1, dto.getStartDay());
        assertEquals(2, dto.getPeakDay());
        assertEquals(0.60f, dto.getPeakValue(), 0.0001);

        verify(simulacionRepository, times(1)).obtenerProyeccion(plantaId, dias);
    }

    @Test
    void execute_emptyProjection_returnsEmptyLists() {
        // Arrange
        ProyeccionHidrica vacia = new ProyeccionHidrica(
                List.of(), List.of(), List.of(), 0, 0, 0f);
        when(simulacionRepository.obtenerProyeccion(plantaId, dias)).thenReturn(vacia);

        // Act
        ProyeccionDto dto = useCase.execute(plantaId, dias);

        // Assert
        assertTrue(dto.getPuntos().isEmpty());
        assertTrue(dto.getBandaSuperior().isEmpty());
        assertTrue(dto.getBandaInferior().isEmpty());
        verify(simulacionRepository).obtenerProyeccion(plantaId, dias);
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        when(simulacionRepository.obtenerProyeccion(anyInt(), anyInt()))
                .thenThrow(new IllegalStateException("Sin datos"));

        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> useCase.execute(plantaId, dias));
        assertEquals("Sin datos", thrown.getMessage());
    }
}
