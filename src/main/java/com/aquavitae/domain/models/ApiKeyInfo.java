package com.aquavitae.domain.models;

public class ApiKeyInfo {
    public Integer id;
    public String nombre;
    public Boolean activa;
    public String expiracion;
    public String fechaRotacion;

    public ApiKeyInfo(Integer id, String nombre, Boolean activa, String expiracion, String fechaRotacion) {
        this.id = id;
        this.nombre = nombre;
        this.activa = activa;
        this.expiracion = expiracion;
        this.fechaRotacion = fechaRotacion;
    }
}