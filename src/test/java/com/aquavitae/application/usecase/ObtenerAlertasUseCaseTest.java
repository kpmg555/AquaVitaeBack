package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.AlertaDto;
import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.repository.AlertaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerAlertasUseCaseTest {

    @Mock
    AlertaRepository alertaRepository;

    @InjectMocks
    ObtenerAlertasUseCase useCase;

    private AlertaDominio alertaDominio;

    @BeforeEach
    void setUp() {
        alertaDominio = new AlertaDominio(
                1, 100, "RIESGO", "Alerta de sequía",
                "Nivel bajo", 0.2, 0.5, LocalDateTime.now());
    }

    @Test
    void execute_happyPath_returnsListOfAlertaDto() {
        // Arrange
        int limit = 5;
        List<AlertaDominio> dominioList = List.of(alertaDominio);
        when(alertaRepository.findRecientes(limit)).thenReturn(dominioList);

        // Act
        List<AlertaDto> result = useCase.execute(limit);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        AlertaDto dto = result.get(0);
        assertEquals(alertaDominio.getId(), dto.getId());
        assertEquals(alertaDominio.getTipo(), dto.getTipo());
        assertEquals(alertaDominio.getTitulo(), dto.getTitulo());
        assertEquals(alertaDominio.getDescripcion(), dto.getDescripcion());
        assertEquals(alertaDominio.getNivelActual(), dto.getNivelActual());
        assertEquals(alertaDominio.getUmbral(), dto.getUmbral());
        assertEquals(alertaDominio.getFecha(), dto.getFecha());

        verify(alertaRepository, times(1)).findRecientes(limit);
    }

    @Test
    void execute_emptyListFromRepository_returnsEmptyList() {
        // Arrange
        int limit = 10;
        when(alertaRepository.findRecientes(limit)).thenReturn(Collections.emptyList());

        // Act
        List<AlertaDto> result = useCase.execute(limit);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(alertaRepository).findRecientes(limit);
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        int limit = 5;
        when(alertaRepository.findRecientes(limit)).thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> useCase.execute(limit));
        assertEquals("DB error", thrown.getMessage());
        verify(alertaRepository).findRecientes(limit);
    }
}