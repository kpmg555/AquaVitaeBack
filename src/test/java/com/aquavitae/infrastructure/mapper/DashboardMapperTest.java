package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.DashboardRiesgo;
import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.infrastructure.entities.PlantaEntity;
import com.aquavitae.infrastructure.entities.UbicacionEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DashboardMapperTest {

    @Test
    void toPlantaConRiesgo_withValidData_createsPlantaRiesgoCorrectly() {
        // Arrange
        PlantaEntity planta = new PlantaEntity();
        planta.setId(1);
        planta.setNombre("Planta Norte");

        UbicacionEntity ubicacion = new UbicacionEntity();
        ubicacion.setLatitud(-33.45);
        ubicacion.setLongitud(-70.65);

        Double indiceHidrico = 0.82;
        String ubicacionNombre = "Santiago Centro";

        // Act
        PlantaRiesgo result = DashboardMapper.toPlantaConRiesgo(planta, ubicacion, indiceHidrico, ubicacionNombre);

        // Assert
        assertEquals(1, result.getId());
        assertEquals("Planta Norte", result.getNombrePlanta());
        assertEquals(-33.45, result.getLatitud());
        assertEquals(-70.65, result.getLongitud());
        assertEquals(0.82, result.getIndiceHidrico());
        assertEquals("ALTO", result.getNivelRiesgo()); // 0.82 >= 0.70 -> ALTO
        assertEquals("Santiago Centro", result.getUbicacionNombre());
    }

    @Test
    void toPlantaConRiesgo_withNullIndice_usesDefaultValue1_0AndClassifiesAlto() {
        // Arrange
        PlantaEntity planta = new PlantaEntity();
        planta.setId(2);
        planta.setNombre("Planta Sur");

        UbicacionEntity ubicacion = new UbicacionEntity();
        ubicacion.setLatitud(-34.0);
        ubicacion.setLongitud(-71.0);

        // Act
        PlantaRiesgo result = DashboardMapper.toPlantaConRiesgo(planta, ubicacion, null, "Rancagua");

        // Assert
        assertEquals(1.0, result.getIndiceHidrico());
        assertEquals("ALTO", result.getNivelRiesgo()); // 1.0 >= 0.70
        assertEquals("Rancagua", result.getUbicacionNombre());
    }

    @Test
    void toPlantaConRiesgo_withUbicacionNombreNull_usesEmptyString() {
        // Arrange
        PlantaEntity planta = new PlantaEntity();
        planta.setId(3);

        UbicacionEntity ubicacion = new UbicacionEntity();
        ubicacion.setLatitud(0.0);
        ubicacion.setLongitud(0.0);

        // Act
        PlantaRiesgo result = DashboardMapper.toPlantaConRiesgo(planta, ubicacion, 0.3, null);

        // Assert
        assertEquals("", result.getUbicacionNombre());
    }

    @Test
    void toDashboardRiesgo_classifiesPlantasAndBuildsResumenCorrectly() {
        // Arrange
        PlantaRiesgo alto = mockPlantaRiesgo(0.85);
        PlantaRiesgo medio = mockPlantaRiesgo(0.55);
        PlantaRiesgo bajo = mockPlantaRiesgo(0.30);
        List<PlantaRiesgo> plantas = List.of(alto, medio, bajo);

        // Act
        DashboardRiesgo dashboard = DashboardMapper.toDashboardRiesgo(plantas);

        // Assert
        assertEquals(3, dashboard.getPlantas().size());
        assertEquals(1, dashboard.getResumen().getAlto());
        assertEquals(1, dashboard.getResumen().getMedio());
        assertEquals(1, dashboard.getResumen().getBajo());
    }

    // Método auxiliar para crear PlantaRiesgo con nivelRiesgo según el índice
    private PlantaRiesgo mockPlantaRiesgo(double indice) {
        String nivel;
        if (indice >= 0.70)
            nivel = "ALTO";
        else if (indice >= 0.45)
            nivel = "MEDIO";
        else
            nivel = "BAJO";
        return new PlantaRiesgo(0, "", 0.0, 0.0, indice, nivel, "");
    }

    // Prueba del método privado clasificarRiesgo (accedemos por reflexión o
    // replicamos lógica)
    // Como el método es privado, lo probamos indirectamente a través de
    // toPlantaConRiesgo.
    @Test
    void clasificacionRiesgo_indirectViaToPlantaConRiesgo_worksForThresholds() {
        // Probamos los umbrales: ALTO >=0.70, MEDIO >=0.45, BAJO <0.45
        PlantaEntity dummyPlanta = new PlantaEntity();
        UbicacionEntity dummyUbic = new UbicacionEntity();

        PlantaRiesgo p1 = DashboardMapper.toPlantaConRiesgo(dummyPlanta, dummyUbic, 0.70, "");
        assertEquals("ALTO", p1.getNivelRiesgo());

        PlantaRiesgo p2 = DashboardMapper.toPlantaConRiesgo(dummyPlanta, dummyUbic, 0.45, "");
        assertEquals("MEDIO", p2.getNivelRiesgo());

        PlantaRiesgo p3 = DashboardMapper.toPlantaConRiesgo(dummyPlanta, dummyUbic, 0.44, "");
        assertEquals("BAJO", p3.getNivelRiesgo());

        PlantaRiesgo p4 = DashboardMapper.toPlantaConRiesgo(dummyPlanta, dummyUbic, 0.69, "");
        assertEquals("MEDIO", p4.getNivelRiesgo()); // 0.69 está entre 0.45 y 0.70
    }
}