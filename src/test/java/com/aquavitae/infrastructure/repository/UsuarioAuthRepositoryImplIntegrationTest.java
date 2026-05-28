package com.aquavitae.infrastructure.repository;

import com.aquavitae.domain.repository.UsuarioAuthRepository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class UsuarioAuthRepositoryImplIntegrationTest {

    @Inject
    UsuarioAuthRepositoryImpl authRepository;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void setUp() {
        // Rol_Permiso debe borrarse antes que Rol por restriccion de clave foranea
        em.createNativeQuery("DELETE FROM Rol_Permiso").executeUpdate();
        em.createNativeQuery("DELETE FROM Usuario").executeUpdate();
        em.createNativeQuery("DELETE FROM Rol").executeUpdate();
        em.createNativeQuery("DELETE FROM Empresa").executeUpdate();

        // Insertar empresa y rol necesarios
        em.createNativeQuery("INSERT INTO Empresa (id, nombre) VALUES (1, 'Empresa Test')").executeUpdate();
        em.createNativeQuery("INSERT INTO Rol (id, nombre) VALUES (1, 'Rol Test')").executeUpdate();
    }

    @Test
    @Transactional
    void testFindByUid_existingActiveUser_returnsData() {
        em.createNativeQuery(
                "INSERT INTO Usuario (uuid, nombre, apellido, correo, id_empresa, id_rol, activo) " +
                        "VALUES ('uid-abc', 'Juan', 'Pérez', 'juan@test.com', 1, 1, 1)")
                .executeUpdate();

        Optional<UsuarioAuthRepository.UsuarioAuthData> result = authRepository.findByUid("uid-abc");
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdEmpresa());
        assertEquals(1, result.get().getIdRol());
        assertEquals("Juan", result.get().getNombre());
        assertEquals("Pérez", result.get().getApellido());
    }

    @Test
    @Transactional
    void testFindByUid_inactiveUser_returnsEmpty() {
        em.createNativeQuery(
                "INSERT INTO Usuario (uuid, nombre, apellido, correo, id_empresa, id_rol, activo) " +
                        "VALUES ('uid-inactive', 'Ana', 'López', 'ana@test.com', 1, 1, 0)")
                .executeUpdate();

        Optional<UsuarioAuthRepository.UsuarioAuthData> result = authRepository.findByUid("uid-inactive");
        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    void testFindByUid_notFound_returnsEmpty() {
        Optional<UsuarioAuthRepository.UsuarioAuthData> result = authRepository.findByUid("nonexistent");
        assertFalse(result.isPresent());
    }
}