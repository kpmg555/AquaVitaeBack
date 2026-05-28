package com.aquavitae.domain.models;

import java.time.LocalDateTime;

public class AuditoriaLog {

    private final Integer id;
    private final Integer idUsuario;
    private final String usuario;
    private final String accion;
    private final String modulo;
    private final String entidad;
    private final String descripcion;
    private final String ip;
    private final String severidad;
    private final String valorAnterior;
    private final String valorNuevo;
    private final LocalDateTime fecha;

    public AuditoriaLog(
            Integer id,
            Integer idUsuario,
            String usuario,
            String accion,
            String modulo,
            String entidad,
            String descripcion,
            String ip,
            String severidad,
            String valorAnterior,
            String valorNuevo,
            LocalDateTime fecha
    ) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.accion = accion;
        this.modulo = modulo;
        this.entidad = entidad;
        this.descripcion = descripcion;
        this.ip = ip;
        this.severidad = severidad;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.fecha = fecha;
    }

    public Integer getId() { return id; }
    public Integer getIdUsuario() { return idUsuario; }
    public String getUsuario() { return usuario; }
    public String getAccion() { return accion; }
    public String getModulo() { return modulo; }
    public String getEntidad() { return entidad; }
    public String getDescripcion() { return descripcion; }
    public String getIp() { return ip; }
    public String getSeveridad() { return severidad; }
    public String getValorAnterior() { return valorAnterior; }
    public String getValorNuevo() { return valorNuevo; }
    public LocalDateTime getFecha() { return fecha; }

    public boolean isInmutable() {
        return true;
    }

    public String getHashIntegridad() {
        String base = id + "|" + idUsuario + "|" + accion + "|" + modulo + "|" + entidad + "|" + fecha;
        return Integer.toHexString(base.hashCode()).toUpperCase();
    }
}