package com.aquavitae.infrastructure.entities;

//   La representación JPA de la tabla Ubicacion en la BD.
//   Solo existe para que Hibernate pueda hacer el mapeo
//   objeto-relacional. No tiene lógica de negocio.
//El dominio no debe saber que existe JPA.

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import java.util.HashSet;
import java.util.Set;
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
    private Float longitud;

    @Column(nullable = false)
    private Float latitud;

    private Integer elevation;

    public UbicacionEntity() {}

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Float getLongitud() { return longitud; }
    public void setLongitud(Float longitud) { this.longitud = longitud; }

    public Float getLatitud() { return latitud; }
    public void setLatitud(Float latitud) { this.latitud = latitud; }

    public Integer getElevation() { return elevation; }
    public void setElevation(Integer elevation) { this.elevation = elevation; }

}
