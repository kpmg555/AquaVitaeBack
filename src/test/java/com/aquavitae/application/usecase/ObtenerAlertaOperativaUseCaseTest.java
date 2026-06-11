package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.AlertaOperativaDto;
import com.aquavitae.domain.models.AlertaOperativa;
import com.aquavitae.domain.repository.AlternativasRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// SCRUM-235 · Pruebas unitarias back — Dashboard Alternativas (Alerta operativa)
@ExtendWith(MockitoExtension.class)
class ObtenerAlertaOperativaUseCaseTest {

    @Mock
    AlternativasRepository repository;

    @InjectMocks
    ObtenerAlertaOperativaUseCase useCase;

    private final int plantaId = 5;
    private AlertaOperativa alerta;

    @BeforeEach
    void setUp() {
        alerta = new AlertaOperativa(
                0.82f, 7,
                new BigDecimal("120000.00"), new BigDecimal("45000.00"),
                30, 60, "Planta Monterrey");
    }

    @Test
    void execute_happyPath_mapsDomainToDto() {
        // Arrange
        when(repository.obtenerAlerta(plantaId)).thenReturn(alerta);

        // Act
        AlertaOperativaDto dto = useCase.execute(plantaId);

        // Assert
        assertNotNull(dto);
        assertEquals("Planta Monterrey", dto.nombrePlanta);
        assertEquals(0.82f, dto.indiceActual, 0.0001);
        assertEquals(7, dto.diasCierreRecomendado);
        assertEquals(new BigDecimal("120000.00"), dto.costoApertura);
        assertEquals(new BigDecimal("45000.00"), dto.costoOperacionEstimada);
        assertEquals(30, dto.diasReaperturaMin);
        assertEquals(60, dto.diasReaperturaMax);

        verify(repository, times(1)).obtenerAlerta(plantaId);
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        when(repository.obtenerAlerta(plantaId))
                .thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> useCase.execute(plantaId));
        assertEquals("DB error", thrown.getMessage());
        verify(repository).obtenerAlerta(plantaId);
    }
}
