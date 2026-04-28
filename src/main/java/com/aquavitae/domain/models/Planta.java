//La ingesta lo usa para saber qué coordenadas consultar y qué umbral tiene cada planta.

package com.aquavitae.domain.models;

import java.util.UUID;

public class Planta {
    private UUID id;
    private String nombre;
    private Float latitud;
    private Float longitud;
    private Float umbralAlerta;
    private UUID idEmpresa;
    private UUID idEstadoSmn;
    private UUID idMunicipioSmn;

    public Planta() {}

    public Planta(UUID id, String nombre, Float latitud, Float longitud, Float umbralAlerta, UUID idEmpresa, UUID idEstadoSmn, UUID idMunicipioSmn) {
        this.id = id;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.umbralAlerta = umbralAlerta;
        this.idEmpresa = idEmpresa;
        this.idEstadoSmn = idEstadoSmn;
        this.idMunicipioSmn = idMunicipioSmn;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Float getLatitud() { return latitud; }
    public void setLatitud(Float latitud) { this.latitud = latitud; }

    public Float getLongitud() { return longitud; }
    public void setLongitud(Float longitud) { this.longitud = longitud; }

    public Float getUmbralAlerta() { return umbralAlerta; }
    public void setUmbralAlerta(Float umbralAlerta) { this.umbralAlerta = umbralAlerta; }

    public UUID getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(UUID idEmpresa) { this.idEmpresa = idEmpresa; }

    public UUID getIdEstadoSmn() { return idEstadoSmn; }
    public void setIdEstadoSmn(UUID idEstadoSmn) { this.idEstadoSmn = idEstadoSmn; }

    public UUID getIdMunicipioSmn() { return idMunicipioSmn; }
    public void setIdMunicipioSmn(UUID idMunicipioSmn) { this.idMunicipioSmn = idMunicipioSmn; }
}