package com.aquavitae.infrastructure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "Ubicacion")
public class UbicacionEntity {
    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @Column(name = "location_id", unique = true)
    private Integer locationId;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private Float latitud;

    @Column(nullable = false)
    private Float longitud;

    private Integer elevation;

    @Column(name = "id_estado_smn")
    private Integer idEstadoSmn;

    @Column(name = "id_municipio_smn")
    private Integer idMunicipioSmn;

    public UbicacionEntity() {}

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public Integer getLocationId() {return locationId;}
    public void setLocationId(Integer locationId) {this.locationId = locationId;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public Float getLatitud() {return latitud;}
    public void setLatitud(Float latitud) {this.latitud = latitud;}

    public Float getLongitud() {return longitud;}
    public void setLongitud(Float longitud) {this.longitud = longitud;}

    public Integer getElevation() {return elevation;}
    public void setElevation(Integer elevation) {this.elevation = elevation;}

    public Integer getIdEstadoSmn() {return idEstadoSmn;}
    public void setIdEstadoSmn(Integer idEstadoSmn) {this.idEstadoSmn = idEstadoSmn;}

    public Integer getIdMunicipioSmn() {return idMunicipioSmn;}
    public void setIdMunicipioSmn(Integer idMunicipioSmn) {this.idMunicipioSmn = idMunicipioSmn;}
}
