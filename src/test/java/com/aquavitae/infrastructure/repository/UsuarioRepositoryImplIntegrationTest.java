package com.aquavitae.infrastructure.repository;

import com.aquavitae.domain.models.Usuario;
import com.aquavitae.infrastructure.entities.EmpresaEntity;
import com.aquavitae.infrastructure.entities.PermisoEntity;
import com.aquavitae.infrastructure.entities.RolEntity;
import com.aquavitae.infrastructure.entities.UsuarioEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para UsuarioRepositoryImpl.
 * Utiliza base de datos H2 en memoria configurada en application.properties de
 * test.
 * Las transacciones se gestionan con @Transactional en cada prueba para aislar
 * los datos.
 */
@QuarkusTest
class UsuarioRepositoryImplIntegrationTest {

    @Inject
    UsuarioRepositoryImpl usuarioRepository;

    @Inject
    EntityManager em;

    private Integer empresaId;
    private Integer rolId;

    /**
     * Limpia las tablas y crea una empresa y un rol de prueba antes de cada test.
     * El orden de borrado respeta las restricciones de clave foranea.
     * Los IDs se generan automaticamente y se almacenan para usarlos en los tests.
     */
    @BeforeEach
    @Transactional
    void setUp() {
        // Rol_Permiso debe borrarse antes que Rol por restriccion de clave foranea
        em.createNativeQuery("DELETE FROM Rol_Permiso").executeUpdate();
        em.createNativeQuery("DELETE FROM Usuario").executeUpdate();
        em.createNativeQuery("DELETE FROM Rol").executeUpdate();
        em.createNativeQuery("DELETE FROM Empresa").executeUpdate();
        
        EmpresaEntity empresa = new EmpresaEntity();
        empresa.setNombre("Empresa Test");
        em.persist(empresa);
        empresaId = empresa.getId();

        RolEntity rol = new RolEntity();
        rol.setNombre("Administrador");
        rol.setDescripcion("Rol admin");
        em.persist(rol);
        rolId = rol.getId();
    }

    /**
     * Verifica que save persista un usuario y que findById lo recupere
     * con todos sus campos correctamente mapeados.
     */
    @Test
    @Transactional
    void testSaveAndFindById() {
        Usuario usuario = new Usuario();
        usuario.setUuid("uuid-123");
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setNombreUsuario("juanp");
        usuario.setCorreo("juan@test.com");
        usuario.setTelefono("5551234");
        usuario.setIdEmpresa(empresaId);
        usuario.setIdRol(rolId);
        usuario.setActivo(true);
        usuario.setAlcanceDatos("TODAS");

        Usuario saved = usuarioRepository.save(usuario);
        assertNotNull(saved.getId());

        Optional<Usuario> found = usuarioRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("juan@test.com", found.get().getCorreo());
        assertEquals(empresaId, found.get().getIdEmpresa());
        assertEquals(rolId, found.get().getIdRol());
        assertTrue(found.get().isActivo());
    }

    /**
     * Verifica que existsByCorreo devuelva true para correos registrados
     * y false para correos inexistentes.
     */
    @Test
    @Transactional
    void testExistsByCorreo() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setUuid("uuid-456");
        entity.setNombre("Ana");
        entity.setApellido("Lopez");
        entity.setCorreo("ana@test.com");
        entity.setIdEmpresa(empresaId);
        entity.setActivo(true);
        em.persist(entity);

        assertTrue(usuarioRepository.existsByCorreo("ana@test.com"));
        assertFalse(usuarioRepository.existsByCorreo("noexiste@test.com"));
    }

    /**
     * Verifica que findActivosConDetalle solo devuelva usuarios activos.
     * El SQL del repositorio hace JOIN con Empresa y Rol, por lo que ambas
     * entidades deben estar asignadas al usuario para que aparezca en el resultado.
     * Se llama flush y clear antes de la consulta para que el repositorio
     * lea desde la BD y no desde el cache de primer nivel de JPA.
     */
    @Test
    @Transactional
    void testFindActivosConDetalle() {
        UsuarioEntity activo = new UsuarioEntity();
        activo.setUuid("activo-1");
        activo.setNombre("Carlos");
        activo.setApellido("Ruiz");
        activo.setCorreo("carlos@test.com");
        activo.setIdEmpresa(empresaId);
        // idRol es obligatorio porque el SQL hace INNER JOIN con la tabla Rol
        activo.setRol(em.getReference(RolEntity.class, rolId));
        activo.setActivo(true);
        em.persist(activo);

        UsuarioEntity inactivo = new UsuarioEntity();
        inactivo.setUuid("inactivo-1");
        inactivo.setNombre("Luis");
        inactivo.setApellido("Mendoza");
        inactivo.setCorreo("luis@test.com");
        inactivo.setIdEmpresa(empresaId);
        inactivo.setRol(em.getReference(RolEntity.class, rolId));
        inactivo.setActivo(false);
        em.persist(inactivo);

        // flush escribe los INSERT pendientes en la BD
        // clear limpia el cache de primer nivel para que la consulta vaya a la BD
        em.flush();
        em.clear();

        List<Usuario> activos = usuarioRepository.findActivosConDetalle(empresaId, 0, 10);
        assertEquals(1, activos.size());
        assertEquals("Carlos Ruiz", activos.get(0).getNombreCompleto());
        assertEquals("carlos@test.com", activos.get(0).getCorreo());
    }

    /**
     * Verifica que countActivos cuente solo usuarios activos
     * y countTotal cuente todos independientemente del estado.
     * Con i=1,2,3: solo i=2 es activo (i % 2 == 0), los demas son inactivos.
     */
    @Test
    @Transactional
    void testCountActivosAndTotal() {
        for (int i = 1; i <= 3; i++) {
            UsuarioEntity u = new UsuarioEntity();
            u.setUuid("u" + i);
            u.setNombre("User" + i);
            u.setApellido("Test");
            u.setCorreo("user" + i + "@test.com");
            u.setIdEmpresa(empresaId);
            u.setActivo(i % 2 == 0);
            em.persist(u);
        }
        assertEquals(1, usuarioRepository.countActivos(empresaId));
        assertEquals(3, usuarioRepository.countTotal(empresaId));
    }

    /**
     * Verifica que desactivar cambie el campo activo a false en la BD.
     * Se hace flush antes del UPDATE para asegurar que el INSERT ya fue ejecutado.
     * Se hace flush y clear despues del UPDATE para que el find posterior
     * lea el estado actualizado desde la BD y no desde el cache de JPA.
     */
    @Test
    @Transactional
    void testDesactivar() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setUuid("to-deactivate");
        entity.setNombre("Test");
        entity.setApellido("Desactivar");
        entity.setCorreo("des@test.com");
        entity.setIdEmpresa(empresaId);
        entity.setActivo(true);
        em.persist(entity);
        // Asegurar que el INSERT llegue a la BD antes de ejecutar el UPDATE
        em.flush();
        Integer id = entity.getId();

        usuarioRepository.desactivar(id);

        // Forzar escritura del UPDATE y limpiar cache para leer estado real desde BD
        em.flush();
        em.clear();

        UsuarioEntity updated = em.find(UsuarioEntity.class, id);
        assertFalse(updated.isActivo());
    }

    /**
     * Verifica que save persista permisos personalizados y que findById los recupere.
     * Se crea un Permiso en la BD, se guarda un usuario con ese modulo en
     * modulosPersonalizados y se verifica que el dominio recuperado los contenga.
     */
    @Test
    @Transactional
    void testSaveAndFindById_withPermisosPersonalizados() {
        PermisoEntity permiso = new PermisoEntity();
        permiso.setClave("INV_READ");
        permiso.setModulo("Inventario");
        permiso.setDescripcion("Lectura de inventario");
        em.persist(permiso);
        em.flush();

        Usuario usuario = new Usuario();
        usuario.setUuid("uuid-permisos");
        usuario.setNombre("Sofia");
        usuario.setApellido("Torres");
        usuario.setNombreUsuario("sofiat");
        usuario.setCorreo("sofia@test.com");
        usuario.setIdEmpresa(empresaId);
        usuario.setIdRol(rolId);
        usuario.setActivo(true);
        usuario.setModulosPersonalizados(Arrays.asList("Inventario"));

        Usuario saved = usuarioRepository.save(usuario);
        assertNotNull(saved.getId());

        em.flush();
        em.clear();

        Optional<Usuario> found = usuarioRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        List<String> modulos = found.get().getModulosPersonalizados();
        assertNotNull(modulos);
        assertTrue(modulos.contains("Inventario"));
    }

    /**
     * Verifica que findModulosPersonalizadosByIds devuelva el mapa correcto
     * para varios usuarios, incluyendo uno sin permisos personalizados.
     */
    @Test
    @Transactional
    void testFindModulosPersonalizadosByIds() {
        PermisoEntity p1 = new PermisoEntity();
        p1.setClave("INV_READ2");
        p1.setModulo("Inventario");
        em.persist(p1);

        PermisoEntity p2 = new PermisoEntity();
        p2.setClave("REP_READ");
        p2.setModulo("Reportes");
        em.persist(p2);
        em.flush();

        // Usuario con dos modulos
        Usuario u1 = new Usuario();
        u1.setUuid("uuid-map-1");
        u1.setNombre("Mario");
        u1.setApellido("Vega");
        u1.setCorreo("mario@test.com");
        u1.setIdEmpresa(empresaId);
        u1.setActivo(true);
        u1.setModulosPersonalizados(Arrays.asList("Inventario", "Reportes"));
        u1 = usuarioRepository.save(u1);

        // Usuario sin modulos personalizados
        Usuario u2 = new Usuario();
        u2.setUuid("uuid-map-2");
        u2.setNombre("Rosa");
        u2.setApellido("Luna");
        u2.setCorreo("rosa@test.com");
        u2.setIdEmpresa(empresaId);
        u2.setActivo(true);
        u2 = usuarioRepository.save(u2);

        em.flush();
        em.clear();

        Map<Integer, List<String>> result = usuarioRepository.findModulosPersonalizadosByIds(
                Arrays.asList(u1.getId(), u2.getId()));

        List<String> modulosU1 = result.get(u1.getId());
        assertNotNull(modulosU1);
        assertEquals(2, modulosU1.size());
        assertTrue(modulosU1.contains("Inventario"));
        assertTrue(modulosU1.contains("Reportes"));

        // u2 no tiene permisos personalizados, no debe aparecer en el mapa
        assertNull(result.get(u2.getId()));
    }
}