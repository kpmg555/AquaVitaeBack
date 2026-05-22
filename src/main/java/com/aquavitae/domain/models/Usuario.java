package com.aquavitae.domain.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Usuario {
    private Integer id;
    private String uuid;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private Integer idEmpresa;
    private String nombreEmpresa;
    private Integer idRol;
    private String nombreRol;
    private boolean activo;
    private LocalDateTime ultimoAcceso;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a",
            new Locale("es", "MX"));

    public Usuario() {
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getUltimoAccesoFormateado() {
        if (ultimoAcceso == null)
            return "—";
        return ultimoAcceso.format(FORMATTER)
                .replace("a. m.", "a.m.")
                .replace("p. m.", "p.m.");
    }
}