package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.Usuario;
import com.aquavitae.infrastructure.entities.PermisoEntity;
import com.aquavitae.infrastructure.entities.UsuarioEntity;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioMapper {

    public static Usuario toDomain(UsuarioEntity e) {
        if (e == null) {
            return null;
        }
        Usuario u = new Usuario();
        u.setId(e.getId());
        u.setUuid(e.getUuid());
        u.setNombre(e.getNombre());
        u.setApellido(e.getApellido());
        u.setNombreUsuario(e.getNombreUsuario());
        u.setCorreo(e.getCorreo());
        u.setTelefono(e.getTelefono());
        u.setIdEmpresa(e.getIdEmpresa());
        u.setActivo(e.isActivo());
        u.setAlcanceDatos(e.getAlcanceDatos());
        u.setIdPlantaAsignada(e.getIdPlantaAsignada());
        u.setUltimoAcceso(e.getUltimoAcceso());
        if (e.getRol() != null) {
            u.setIdRol(e.getRol().getId());
            u.setNombreRol(e.getRol().getNombre());
        }
        // Incluir módulos personalizados solo cuando la colección está cargada
        if (e.getPermisosPersonalizados() != null
                && Hibernate.isInitialized(e.getPermisosPersonalizados())
                && !e.getPermisosPersonalizados().isEmpty()) {
            List<String> modulos = e.getPermisosPersonalizados().stream()
                    .map(PermisoEntity::getModulo)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            u.setModulosPersonalizados(modulos);
        }
        return u;
    }

    public static UsuarioEntity toEntity(Usuario u) {
        if (u == null) {
            return null;
        }
        UsuarioEntity e = new UsuarioEntity();
        e.setId(u.getId());
        e.setUuid(u.getUuid());
        e.setNombre(u.getNombre());
        e.setApellido(u.getApellido());
        e.setNombreUsuario(u.getNombreUsuario());
        e.setCorreo(u.getCorreo());
        e.setTelefono(u.getTelefono());
        e.setIdEmpresa(u.getIdEmpresa());
        e.setActivo(u.isActivo());
        e.setAlcanceDatos(u.getAlcanceDatos() != null ? u.getAlcanceDatos() : "TODAS");
        e.setIdPlantaAsignada(u.getIdPlantaAsignada());
        e.setUltimoAcceso(u.getUltimoAcceso());
        // El rol y los permisos se asignan por separado en el repositorio
        return e;
    }
}
