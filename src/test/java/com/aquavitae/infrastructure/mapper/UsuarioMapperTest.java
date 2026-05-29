package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.Usuario;
import com.aquavitae.infrastructure.entities.PermisoEntity;
import com.aquavitae.infrastructure.entities.RolEntity;
import com.aquavitae.infrastructure.entities.UsuarioEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    @Test
    void toDomain_mapsAllFieldsCorrectly() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(1);
        entity.setUuid("abc-123");
        entity.setNombre("Carlos");
        entity.setApellido("Ruiz");
        entity.setNombreUsuario("carlosruiz");
        entity.setCorreo("carlos@example.com");
        entity.setTelefono("5550000");
        entity.setIdEmpresa(10);
        entity.setActivo(true);
        entity.setAlcanceDatos("TODAS");
        entity.setIdPlantaAsignada(7);

        // Crear rol simulado
        RolEntity rol = new RolEntity();
        rol.setId(2);
        rol.setNombre("ADMIN");
        entity.setRol(rol);

        Usuario domain = UsuarioMapper.toDomain(entity);

        assertEquals(1, domain.getId());
        assertEquals("abc-123", domain.getUuid());
        assertEquals("Carlos", domain.getNombre());
        assertEquals("Ruiz", domain.getApellido());
        assertEquals("carlosruiz", domain.getNombreUsuario());
        assertEquals("carlos@example.com", domain.getCorreo());
        assertEquals("5550000", domain.getTelefono());
        assertEquals(10, domain.getIdEmpresa());
        assertTrue(domain.isActivo());
        assertEquals("TODAS", domain.getAlcanceDatos());
        assertEquals(7, domain.getIdPlantaAsignada());
        // Verificar mapeo del rol
        assertEquals(2, domain.getIdRol());
        assertEquals("ADMIN", domain.getNombreRol());
    }

    @Test
    void toDomain_withNullEntity_returnsNull() {
        assertNull(UsuarioMapper.toDomain(null));
    }

    @Test
    void toEntity_mapsAllFieldsCorrectly() {
        Usuario domain = new Usuario();
        domain.setId(5);
        domain.setUuid("xyz-789");
        domain.setNombre("Laura");
        domain.setApellido("Mendoza");
        domain.setNombreUsuario("lauram");
        domain.setCorreo("laura@example.com");
        domain.setTelefono("5551111");
        domain.setIdEmpresa(20);
        domain.setActivo(false);
        domain.setAlcanceDatos("PLANTA_ONLY");
        domain.setIdPlantaAsignada(12);
        // El rol no se mapea, por lo que no se debe esperar en la entidad

        UsuarioEntity entity = UsuarioMapper.toEntity(domain);

        // El ID se mapea (contrario a lo que suponía la prueba original)
        assertEquals(5, entity.getId());
        assertEquals("xyz-789", entity.getUuid());
        assertEquals("Laura", entity.getNombre());
        assertEquals("Mendoza", entity.getApellido());
        assertEquals("lauram", entity.getNombreUsuario());
        assertEquals("laura@example.com", entity.getCorreo());
        assertEquals("5551111", entity.getTelefono());
        assertEquals(20, entity.getIdEmpresa());
        assertFalse(entity.isActivo());
        assertEquals("PLANTA_ONLY", entity.getAlcanceDatos());
        assertEquals(12, entity.getIdPlantaAsignada());
        // El rol debe ser nulo porque el mapper no lo asigna
        assertNull(entity.getRol());
    }

    @Test
    void toEntity_withNullDomain_returnsNull() {
        assertNull(UsuarioMapper.toEntity(null));
    }

    @Test
    void toDomain_withPermisosPersonalizados_setsModulosPersonalizados() {
        PermisoEntity p1 = new PermisoEntity();
        p1.setId(1);
        p1.setClave("INV_READ");
        p1.setModulo("Inventario");

        PermisoEntity p2 = new PermisoEntity();
        p2.setId(2);
        p2.setClave("REP_READ");
        p2.setModulo("Reportes");

        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(10);
        entity.setUuid("uuid-permisos");
        entity.setNombre("Test");
        entity.setApellido("User");
        entity.setCorreo("test@example.com");
        entity.setActivo(true);
        entity.setPermisosPersonalizados(new HashSet<>(Arrays.asList(p1, p2)));

        Usuario domain = UsuarioMapper.toDomain(entity);

        List<String> modulos = domain.getModulosPersonalizados();
        assertNotNull(modulos);
        assertEquals(2, modulos.size());
        assertTrue(modulos.contains("Inventario"));
        assertTrue(modulos.contains("Reportes"));
    }

    @Test
    void toDomain_withEmptyPermisosPersonalizados_modulosPersonalizadosIsNull() {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(11);
        entity.setUuid("uuid-empty");
        entity.setNombre("Empty");
        entity.setApellido("Permisos");
        entity.setCorreo("empty@example.com");
        entity.setActivo(true);
        entity.setPermisosPersonalizados(new HashSet<>());

        Usuario domain = UsuarioMapper.toDomain(entity);

        assertNull(domain.getModulosPersonalizados());
    }
}