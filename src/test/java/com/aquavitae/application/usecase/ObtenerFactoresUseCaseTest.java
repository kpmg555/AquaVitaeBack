package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.FactorEvaluacionDto;
import com.aquavitae.domain.models.FactorEvaluacion;
import com.aquavitae.domain.repository.AlternativasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// SCRUM-235 · Pruebas unitarias back — Dashboard Alternativas (Factores)
@ExtendWith(MockitoExtension.class)
class ObtenerFactoresUseCaseTest {

    @Mock
    AlternativasRepository repository;

    @InjectMocks
    ObtenerFactoresUseCase useCase;

    private final int plantaId = 5;
    private FactorEvaluacion factor;

    @BeforeEach
    void setUp() {
        factor = new FactorEvaluacion("Disponibilidad de agua", "water", 4, "#2ea36b");
    }

    @Test
    void execute_happyPath_mapsListToDtos() {
        // Arrange
        when(repository.obtenerFactores(plantaId)).thenReturn(List.of(factor));

        // Act
        List<FactorEvaluacionDto> result = useCase.execute(plantaId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        FactorEvaluacionDto dto = result.get(0);
        assertEquals("Disponibilidad de agua", dto.nombre);
        assertEquals("water", dto.icono);
        assertEquals(4, dto.puntos);
        assertEquals("#2ea36b", dto.color);

        verify(repository, times(1)).obtenerFactores(plantaId);
    }

    @Test
    void execute_emptyList_returnsEmptyList() {
        // Arrange
        when(repository.obtenerFactores(plantaId)).thenReturn(List.of());

        // Act
        List<FactorEvaluacionDto> result = useCase.execute(plantaId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository).obtenerFactores(plantaId);
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        when(repository.obtenerFactores(plantaId))
                .thenThrow(new RuntimeException("Falla"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> useCase.execute(plantaId));
        verify(repository).obtenerFactores(plantaId);
    }
}
