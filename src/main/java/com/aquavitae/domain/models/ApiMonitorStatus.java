package com.aquavitae.domain.models;

public class ApiMonitorStatus {
    public String nombreApi;
    public String endpoint;
    public String estado;
    public Integer ultimoCodigo;
    public String mensaje;
    public Integer erroresActivos;

    public ApiMonitorStatus(String nombreApi, String endpoint, String estado, Integer ultimoCodigo, String mensaje, Integer erroresActivos) {
        this.nombreApi = nombreApi;
        this.endpoint = endpoint;
        this.estado = estado;
        this.ultimoCodigo = ultimoCodigo;
        this.mensaje = mensaje;
        this.erroresActivos = erroresActivos;
    }
}