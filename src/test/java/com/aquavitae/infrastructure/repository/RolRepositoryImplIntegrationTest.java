package com.aquavitae.infrastructure.repository;

import com.aquavitae.domain.models.Rol;
import com.aquavitae.infrastructure.entities.PermisoEntity;
import com.aquavitae.infrastructure.entities.RolEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para RolRepositoryImpl.
 * Utiliza base de datos H2 en memoria configurada en application.properties de
 * test.
 * Cada prueba se ejecuta dentro de una transacción para aislar los datos.
 */
@QuarkusTest
class RolRepositoryImplIntegrationTest {

    @Inject
    RolRepositoryImpl rolRepository;

    @Inject
    EntityManager em;

    /**
     * Limpia las tablas de permisos y roles antes de cada prueba
     * para garantizar un estado inicial limpio.
     * El orden de borrado respeta las restricciones de clave foránea.
     */
    @BeforeEach
    @Transactional
    void setUp() {
        em.createNativeQuery("DELETE FROM Rol_Permiso").executeUpdate();
        em.createNativeQuery("DELETE FROM Permiso").executeUpdate();
        em.createNativeQuery("DELETE FROM Rol").executeUpdate();
    }

    /**
     * Verifica que findAll devuelva todos los roles persistidos en la base de
     * datos.
     */
    @Test
    @Transactional
    void testFindAll() {
        RolEntity rol1 = new RolEntity();
        rol1.setNombre("Admin");
        rol1.setDescripcion("Administrador");
        em.persist(rol1);

        RolEntity rol2 = new RolEntity();
        rol2.setNombre("User");
        rol2.setDescripcion("Usuario normal");
        em.persist(rol2);

        List<Rol> roles = rolRepository.findAll();
        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(r -> "Admin".equals(r.getNombre())));
        assertTrue(roles.stream().anyMatch(r -> "User".equals(r.getNombre())));
    }

    /**
     * Verifica que findById cargue correctamente los permisos asociados al rol.
     * El mapper agrupa los permisos por modulo (distinct), por lo que dos permisos
     * del mismo modulo producen un unico elemento en la lista de permisos del
     * dominio.
     * Se usa HashSet mutable porque JPA necesita poder modificar la coleccion
     * internamente; Set.of() devuelve un set inmutable que causa
     * UnsupportedOperationException.
     */
    @Test
    @Transactional
    void testFindByIdWithPermisos() {
        RolEntity rol = new RolEntity();
        rol.setNombre("Editor");
        rol.setDescripcion("Puede editar");
        em.persist(rol);

        PermisoEntity p1 = new PermisoEntity();
        p1.setModulo("usuarios");
        p1.setClave("usuarios.leer");
        em.persist(p1);

        PermisoEntity p2 = new PermisoEntity();
        p2.setModulo("usuarios");
        p2.setClave("usuarios.editar");
        em.persist(p2);

        // HashSet mutable requerido: JPA modifica la coleccion al hacer merge
        rol.setPermisos(new HashSet<>(Set.of(p1, p2)));
        em.merge(rol);

        // flush sincroniza los cambios pendientes con la BD
        // clear elimina el cache de primer nivel para forzar lectura desde BD
        em.flush();
        em.clear();

        Rol found = rolRepository.findById(rol.getId()).orElseThrow();
        assertEquals("Editor", found.getNombre());
        assertNotNull(found.getPermisos());
        // Ambos permisos son del mismo modulo "usuarios", el mapper los agrupa en uno
        assertEquals(1, found.getPermisos().size());
        assertTrue(found.getPermisos().contains("usuarios"));
        // totalPermisos refleja el numero real de registros, no el de modulos distintos
        assertEquals(2, found.getTotalPermisos());
    }

    /**
     * Verifica que countTotalPermisos cuente correctamente todas las asignaciones
     * de permisos a roles, incluyendo permisos compartidos entre roles.
     * rol1 tiene p1 y p2, rol2 tiene p2 y p3: total = 4 asignaciones.
     * Se usa HashSet mutable por la misma razon que en testFindByIdWithPermisos.
     */
    @Test
    @Transactional
    void testCountTotalPermisos() {
        RolEntity rol1 = new RolEntity();
        rol1.setNombre("Rol1");
        em.persist(rol1);

        RolEntity rol2 = new RolEntity();
        rol2.setNombre("Rol2");
        em.persist(rol2);

        PermisoEntity p1 = new PermisoEntity();
        p1.setModulo("m1");
        p1.setClave("m1.clave");
        em.persist(p1);

        PermisoEntity p2 = new PermisoEntity();
        p2.setModulo("m2");
        p2.setClave("m2.clave");
        em.persist(p2);

        PermisoEntity p3 = new PermisoEntity();
        p3.setModulo("m3");
        p3.setClave("m3.clave");
        em.persist(p3);

        // HashSet mutable requerido: JPA modifica la coleccion al hacer merge
        rol1.setPermisos(new HashSet<>(Set.of(p1, p2)));
        rol2.setPermisos(new HashSet<>(Set.of(p2, p3)));
        em.merge(rol1);
        em.merge(rol2);

        // Sincronizar con BD y limpiar cache antes de ejecutar la consulta
        em.flush();
        em.clear();

        int total = rolRepository.countTotalPermisos();
        assertEquals(4, total);
    }
}