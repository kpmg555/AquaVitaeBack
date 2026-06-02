package com.aquavitae.domain.models;

import java.util.List;

public class AuthenticatedUser {
    private final String uid;
    private final String email;

    private final Integer idEmpresa;
    private final Integer idRol;
    private final String fullName;

    private final String nombre;
    private final String apellido;
    private final String rol;
    private final List<String> permisos;

    public AuthenticatedUser(String uid, String email) {
        this(
                uid,
                email,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of()
        );
    }

    public AuthenticatedUser(
            String uid,
            String email,
            Integer idEmpresa,
            Integer idRol,
            String fullName
    ) {
        this(
                uid,
                email,
                idEmpresa,
                idRol,
                fullName,
                null,
                null,
                null,
                List.of()
        );
    }

    public AuthenticatedUser(
            String uid,
            String email,
            String nombre,
            String apellido,
            String rol,
            List<String> permisos
    ) {
        this(
                uid,
                email,
                null,
                null,
                nombre != null && apellido != null ? nombre + " " + apellido : null,
                nombre,
                apellido,
                rol,
                permisos
        );
    }

    public AuthenticatedUser(
            String uid,
            String email,
            Integer idEmpresa,
            Integer idRol,
            String fullName,
            String nombre,
            String apellido,
            String rol,
            List<String> permisos
    ) {
        this.uid = uid;
        this.email = email;
        this.idEmpresa = idEmpresa;
        this.idRol = idRol;
        this.fullName = fullName;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.permisos = permisos != null ? permisos : List.of();
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public String getFullName() {
        return fullName;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getRol() {
        return rol;
    }

    public List<String> getPermisos() {
        return permisos;
    }
}