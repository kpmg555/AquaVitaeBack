package com.aquavitae.domain.models;

public class ApiAlert {
    public String nombreApi;
    public String endpoint;
    public Integer codigoError;
    public String mensaje;
    public String severidad;

    public ApiAlert(String nombreApi, String endpoint, Integer codigoError, String mensaje, String severidad) {
        this.nombreApi = nombreApi;
        this.endpoint = endpoint;
        this.codigoError = codigoError;
        this.mensaje = mensaje;
        this.severidad = severidad;
    }
}