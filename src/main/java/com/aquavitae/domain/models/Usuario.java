package com.aquavitae.domain.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class Usuario {
    private Integer id;
    private String uuid;
    private String nombre;
    private String apellido;
    private String nombreUsuario;
    private String correo;
    private String telefono;
    private Integer idEmpresa;
    private String nombreEmpresa;
    private Integer idRol;
    private String nombreRol;
    private boolean activo;
    private String alcanceDatos;
    private Integer idPlantaAsignada;
    private LocalDateTime ultimoAcceso;
    // null = hereda permisos del rol; non-null = permisos personalizados del usuario
    private List<String> modulosPersonalizados;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a",
            new Locale("es", "MX"));

    public Usuario() {
        // Construsctor por defecto: Jackson lo necesita para deserializar JSON 
        // Se llena mediante setters hechos por el mapper
    }

    // Getters y Setters (todos)
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

    public LocalDateTime getUltimoAcceso() {
        return ultimoAcceso;
    }

    public void setUltimoAcceso(LocalDateTime ultimoAcceso) {
        this.ultimoAcceso = ultimoAcceso;
    }

    public List<String> getModulosPersonalizados() {
        return modulosPersonalizados;
    }

    public void setModulosPersonalizados(List<String> modulosPersonalizados) {
        this.modulosPersonalizados = modulosPersonalizados;
    }

    // Método para obtener nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    // Método para formatear último acceso (para enviar al frontend)
    public String getUltimoAccesoFormateado() {
        if (ultimoAcceso == null)
            return null;
        return ultimoAcceso.format(FORMATTER)
                .replace("a. m.", "a.m.")
                .replace("p. m.", "p.m.");
    }
}