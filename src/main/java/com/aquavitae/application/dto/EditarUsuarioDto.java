package com.aquavitae.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class EditarUsuarioDto {
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    private String nombreUsuario;
    @NotBlank
    @Email
    private String correo;
    private String telefono;
    private boolean activo;
    @NotNull
    private Integer idRol;
    private String nuevaContrasena;
    private String alcanceDatos;
    private Integer idPlantaAsignada;
    // null = no cambiar permisos; lista = reemplazar permisos del usuario
    private List<String> modulos;

    // getters/setters
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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }

    public String getAlcanceDatos() {
        return alcanceDatos;
    }

    public void setAlcanceDatos(String alcanceDatos) {
        this.alcanceDatos = alcanceDatos;
    }

    public Integer getIdPlantaAsignada() {
        return idPlantaAsignada;
    }

    public void setIdPlantaAsignada(Integer idPlantaAsignada) {
        this.idPlantaAsignada = idPlantaAsignada;
    }

    public List<String> getModulos() {
        return modulos;
    }

    public void setModulos(List<String> modulos) {
        this.modulos = modulos;
    }
}
