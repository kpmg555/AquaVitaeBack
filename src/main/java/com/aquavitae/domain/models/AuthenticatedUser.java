package com.aquavitae.domain.models;

import java.util.List;

public class AuthenticatedUser {

    private final String uid;
    private final String email;
    private final String nombre;
    private final String apellido;
    private final String rol;
    private final List<String> permisos;

    public AuthenticatedUser(String uid, String email) {
        this.uid = uid;
        this.email = email;
        this.nombre = null;
        this.apellido = null;
        this.rol = null;
        this.permisos = List.of();
    }

    public AuthenticatedUser(
            String uid,
            String email,
            String nombre,
            String apellido,
            String rol,
            List<String> permisos
    ) {
        this.uid = uid;
        this.email = email;
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