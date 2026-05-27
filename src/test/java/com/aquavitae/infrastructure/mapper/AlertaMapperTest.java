package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.infrastructure.entities.AlertaEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AlertaMapperTest {

    @Test
    void toDomain_mapsAllFieldsCorrectly() {
        // Arrange
        AlertaEntity entity = new AlertaEntity();
        entity.setId(10);
        entity.setIdPlanta(5);
        entity.setTipo("SEQUIA");
        entity.setTitulo("Bajo nivel");
        entity.setDescripcion("Riesgo crítico");
        entity.setNivelActual(0.2);
        entity.setUmbral(0.5);
        entity.setFecha(LocalDateTime.of(2025, 1, 15, 10, 0));

        // Act
        AlertaDominio domain = AlertaMapper.toDomain(entity);

        // Assert
        assertEquals(10, domain.getId());
        assertEquals(5, domain.getIdPlanta());
        assertEquals("SEQUIA", domain.getTipo());
        assertEquals("Bajo nivel", domain.getTitulo());
        assertEquals("Riesgo crítico", domain.getDescripcion());
        assertEquals(0.2, domain.getNivelActual());
        assertEquals(0.5, domain.getUmbral());
        assertEquals(entity.getFecha(), domain.getFecha());
    }

    @Test
    void toDomain_withNullFields_handlesGracefully() {
        // Arrange
        AlertaEntity entity = new AlertaEntity(); // todos los campos nulos

        // Act
        AlertaDominio domain = AlertaMapper.toDomain(entity);

        // Assert
        assertNull(domain.getId());
        assertNull(domain.getIdPlanta());
        assertNull(domain.getTipo());
        assertNull(domain.getTitulo());
        assertNull(domain.getDescripcion());
        assertNull(domain.getNivelActual());
        assertNull(domain.getUmbral());
        assertNull(domain.getFecha());
    }

    @Test
    void toEntity_mapsOnlyConfiguredFields_doesNotMapIdOrIdPlantaOrUmbral() {
        // ATENCIÓN: El mapper actual en toEntity solo settea tipo, titulo, descripcion,
        // nivelActual, fecha.
        // Esta prueba refleja ese comportamiento real, aunque pueda ser incompleto.
        // Arrange
        AlertaDominio domain = new AlertaDominio(
                99, // id
                77, // idPlanta
                "RIESGO", "Título", "Descripción",
                0.3, // nivelActual
                0.8, // umbral
                LocalDateTime.now());

        // Act
        AlertaEntity entity = AlertaMapper.toEntity(domain);

        // Assert
        assertNull(entity.getId()); // no se mapea
        assertNull(entity.getIdPlanta()); // no se mapea
        assertNull(entity.getUmbral()); // no se mapea
        assertEquals(domain.getTipo(), entity.getTipo());
        assertEquals(domain.getTitulo(), entity.getTitulo());
        assertEquals(domain.getDescripcion(), entity.getDescripcion());
        assertEquals(domain.getNivelActual(), entity.getNivelActual());
        assertEquals(domain.getFecha(), entity.getFecha());
    }
}