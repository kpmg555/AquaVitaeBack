package com.aquavitae.domain.models;

public class AuthenticatedUser {
    private final String uid;
    private final String email;
    private final Integer idEmpresa;
    private final Integer idRol;
    private final String fullName;

    public AuthenticatedUser(String uid, String email) {
        this(uid, email, null, null, null);
    }

    public AuthenticatedUser(String uid, String email, Integer idEmpresa, Integer idRol, String fullName) {
        this.uid = uid;
        this.email = email;
        this.idEmpresa = idEmpresa;
        this.idRol = idRol;
        this.fullName = fullName;
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
}