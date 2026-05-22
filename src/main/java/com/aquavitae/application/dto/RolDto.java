package com.aquavitae.application.dto;

import java.util.List;

public class RolDto {
    private Integer id;
    private String nombre;
    private String descripcion;
    private List<String> permisos;
    private int totalPermisos;

    public RolDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    public int getTotalPermisos() {
        return totalPermisos;
    }

    public void setTotalPermisos(int totalPermisos) {
        this.totalPermisos = totalPermisos;
    }
}