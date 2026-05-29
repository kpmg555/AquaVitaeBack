package com.aquavitae.application.dto;

import java.util.List;

public class UsuarioDto {
    private Integer id;
    private String nombreCompleto;
    private String correo;
    private String nombreRol;
    private Integer idRol;
    private boolean activo;
    private String ultimoAcceso;
    private String nombreEmpresa;
    // null = hereda del rol; lista = permisos personalizados activos
    private List<String> modulosEfectivos;

    // getters/setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(String ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public List<String> getModulosEfectivos() {
        return modulosEfectivos;
    }

    public void setModulosEfectivos(List<String> modulosEfectivos) {
        this.modulosEfectivos = modulosEfectivos;
    }
}