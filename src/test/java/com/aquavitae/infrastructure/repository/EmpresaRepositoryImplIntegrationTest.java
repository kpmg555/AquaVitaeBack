package com.aquavitae.infrastructure.repository;

import com.aquavitae.application.dto.EmpresaDto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EmpresaRepositoryImplIntegrationTest {

    @Inject
    EmpresaRepositoryImpl empresaRepository;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setUp() {
        em.createNativeQuery("DELETE FROM Empresa").executeUpdate();
    }

    @Test
    @Transactional
    void testListarTodas() {
        // Insertar empresas
        em.createNativeQuery("INSERT INTO Empresa (nombre) VALUES ('Empresa A')").executeUpdate();
        em.createNativeQuery("INSERT INTO Empresa (nombre) VALUES ('Empresa B')").executeUpdate();

        List<EmpresaDto> empresas = empresaRepository.listarTodas();
        assertEquals(2, empresas.size());
        assertTrue(empresas.stream().anyMatch(e -> "Empresa A".equals(e.getNombre())));
        assertTrue(empresas.stream().anyMatch(e -> "Empresa B".equals(e.getNombre())));
    }
}