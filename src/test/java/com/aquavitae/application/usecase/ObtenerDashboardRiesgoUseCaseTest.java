package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.DashboardRiesgoDto;
import com.aquavitae.domain.models.DashboardRiesgo;
import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.domain.models.ResumenRiesgo;
import com.aquavitae.domain.repository.DashboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerDashboardRiesgoUseCaseTest {

    @Mock
    DashboardRepository dashboardRepository;

    @InjectMocks
    ObtenerDashboardRiesgoUseCase useCase;

    private List<PlantaRiesgo> plantas;
    private ResumenRiesgo resumen;
    private DashboardRiesgo dashboard;

    @BeforeEach
    void setUp() {
        plantas = List.of(
                new PlantaRiesgo(1, "Planta A", -33.0, -70.0, 0.85, "ALTO", "Santiago"),
                new PlantaRiesgo(2, "Planta B", -34.0, -71.0, 0.50, "MEDIO", "Valparaíso"));
        resumen = new ResumenRiesgo(1, 1, 0);
        dashboard = new DashboardRiesgo(plantas, resumen);
    }

    @Test
    void execute_happyPath_returnsDashboardDtoWithCorrectMapping() {
        // Arrange
        when(dashboardRepository.obtenerDashboard()).thenReturn(dashboard);

        // Act
        DashboardRiesgoDto result = useCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getPlantas().size());
        assertEquals(1, result.getResumen().getAlto());
        assertEquals(1, result.getResumen().getMedio());
        assertEquals(0, result.getResumen().getBajo());

        // Verificar mapeo de la primera planta
        DashboardRiesgoDto.PlantaRiesgoDto primera = result.getPlantas().get(0);
        assertEquals(plantas.get(0).getId(), primera.getIdPlanta());
        assertEquals(plantas.get(0).getNombrePlanta(), primera.getNombrePlanta());
        assertEquals(plantas.get(0).getLatitud(), primera.getLatitud());
        assertEquals(plantas.get(0).getLongitud(), primera.getLongitud());
        assertEquals(plantas.get(0).getIndiceHidrico(), primera.getIndiceHidrico());
        assertEquals(plantas.get(0).getNivelRiesgo(), primera.getNivelRiesgo());
        assertEquals(plantas.get(0).getUbicacionNombre(), primera.getUbicacionNombre());

        verify(dashboardRepository, times(1)).obtenerDashboard();
    }

    @Test
    void execute_repositoryReturnsEmptyPlantasList_returnsDtoWithEmptyListAndZeroResumen() {
        // Arrange
        DashboardRiesgo emptyDashboard = new DashboardRiesgo(List.of(), new ResumenRiesgo(0, 0, 0));
        when(dashboardRepository.obtenerDashboard()).thenReturn(emptyDashboard);

        // Act
        DashboardRiesgoDto result = useCase.execute();

        // Assert
        assertNotNull(result);
        assertTrue(result.getPlantas().isEmpty());
        assertEquals(0, result.getResumen().getAlto());
        assertEquals(0, result.getResumen().getMedio());
        assertEquals(0, result.getResumen().getBajo());
        verify(dashboardRepository).obtenerDashboard();
    }

    @Test
    void execute_repositoryThrowsException_propagatesException() {
        // Arrange
        when(dashboardRepository.obtenerDashboard()).thenThrow(new IllegalStateException("Connection failed"));

        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> useCase.execute());
        assertEquals("Connection failed", thrown.getMessage());
        verify(dashboardRepository).obtenerDashboard();
    }
}