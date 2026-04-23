package com.aquavitae.infrastructure.entities;

//   La representación JPA de la tabla Ubicacion en la BD.
//   Solo existe para que Hibernate pueda hacer el mapeo
//   objeto-relacional. No tiene lógica de negocio.
//El dominio no debe saber que existe JPA.


import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "Planta")
public class PlantaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false, length = 300)
    private String nombre;

    // Relación con Ubicacion para obtener lat/lon
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion")
    private UbicacionEntity ubicacion;

    @Column(name = "id_empresa")
    private Integer idEmpresa;

    @Column(name = "id_region")
    private Integer idRegion;

    public PlantaEntity() {}

    public UUID getId() {return id;}
    public void setId(UUID id) {this.id = id;}

    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public UbicacionEntity getUbicacion() {return ubicacion;}
    public void setUbicacion(UbicacionEntity ubicacion) {this.ubicacion = ubicacion;}

    public Integer getIdEmpresa() {return idEmpresa;}
    public void setIdEmpresa(Integer idEmpresa) {this.idEmpresa = idEmpresa;}

    public Integer getIdRegion() {return idRegion;}
    public void setIdRegion(Integer idRegion) {this.idRegion = idRegion;}

}
