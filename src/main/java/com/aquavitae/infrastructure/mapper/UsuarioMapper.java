package org.acme.infrastructure.mapper;

import org.acme.domain.models.Usuario;
import org.acme.infrastructure.entities.UsuarioEntity;
import org.hibernate.Hibernate;

public class UsuarioMapper {

    public static Usuario toDomain(UsuarioEntity e) {
        Usuario u = new Usuario();
        u.setId(e.getId());
        u.setUuid(e.getUuid());
        u.setNombre(e.getNombre());
        u.setApellido(e.getApellido());
        u.setCorreo(e.getCorreo());
        u.setTelefono(e.getTelefono());
        u.setIdEmpresa(e.getIdEmpresa());
        u.setActivo(e.isActivo());
        u.setUltimoAcceso(e.getUltimoAcceso());

        if (e.getRol() != null && Hibernate.isInitialized(e.getRol())) {
            u.setIdRol(e.getRol().getId());
            u.setNombreRol(e.getRol().getNombre());
        }

        if (e.getPlanta() != null && Hibernate.isInitialized(e.getPlanta())) {
            u.setIdPlanta(e.getPlanta().getId());
            u.setNombrePlanta(e.getPlanta().getNombre());
        }

        if (e.getRegion() != null && Hibernate.isInitialized(e.getRegion())) {
            u.setIdRegion(e.getRegion().getId());
            u.setNombreRegion(e.getRegion().getNombre());
        }

        return u;
    }

    public static UsuarioEntity toEntity(Usuario u) {
        UsuarioEntity e = new UsuarioEntity();
        e.setId(u.getId());
        e.setUuid(u.getUuid());
        e.setNombre(u.getNombre());
        e.setApellido(u.getApellido());
        e.setCorreo(u.getCorreo());
        e.setTelefono(u.getTelefono());
        e.setIdEmpresa(u.getIdEmpresa());
        e.setActivo(u.isActivo());
        e.setUltimoAcceso(u.getUltimoAcceso());
        // rol, planta y region se asocian por referencia en el repositorio
        return e;
    }

    private UsuarioMapper() {}
}