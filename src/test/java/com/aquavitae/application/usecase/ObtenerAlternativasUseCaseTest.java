package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.AlternativaUbicacionDto;
import com.aquavitae.domain.models.AlternativaUbicacion;
import com.aquavitae.domain.repository.AlternativasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// SCRUM-235 · Pruebas unitarias back — Dashboard Alternativas (Ubicaciones)
@ExtendWith(MockitoExtension.class)
class ObtenerAlternativasUseCaseTest {

    @Mock
    AlternativasRepository repository;

    @InjectMocks
    ObtenerAlternativasUseCase useCase;

    private final int plantaId = 5;
    private AlternativaUbicacion alternativa;

    @BeforeEach
    void setUp() {
        alternativa = new AlternativaUbicacion(
                5, "Saltillo", "Coahuila", "bajo", "Riesgo bajo",
                new BigDecimal("80000.00"), new BigDecimal("60000.00"), new BigDecimal("140000.00"),
                15, 30, 45, true);
    }

    @Test
    void execute_happyPath_mapsListToDtos() {
        // Arrange
        when(repository.obtenerAlternativas(plantaId)).thenReturn(List.of(alternativa));

        // Act
        List<AlternativaUbicacionDto> result = useCase.execute(plantaId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        AlternativaUbicacionDto dto = result.get(0);
        assertEquals(5, dto.plantaId);
        assertEquals("Saltillo", dto.nombre);
        assertEquals("Coahuila", dto.estado);
        assertEquals("bajo", dto.riesgo);
        assertEquals("Riesgo bajo", dto.riesgoLabel);
        assertEquals(new BigDecimal("140000.00"), dto.costoTotal);
        assertEquals(15, dto.tiempoCierreDias);
        assertEquals(30, dto.tiempoAperturaMin);
        assertEquals(45, dto.tiempoAperturaMax);
        assertTrue(dto.recomendada);

        verify(repository, times(1)).obtenerAlternativas(plantaId);
    }

    @Test
    void execute_emptyList_returnsEmptyList() {
        // Arrange
        when(repository.obtenerAlternativas(plantaId)).thenReturn(List.of());

        // Act
        List<AlternativaUbicacionDto> result = useCase.execute(plantaId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).obtenerAlternativas(plantaId);
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        when(repository.obtenerAlternativas(plantaId))
                .thenThrow(new IllegalStateException("Sin datos"));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> useCase.execute(plantaId));
        verify(repository).obtenerAlternativas(plantaId);
    }
}
