package com.aquavitae.infrastructure.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
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

    @Column(name = "id_empresa")
    private Integer idEmpresa;

    @Column(name = "id_region")
    private Integer idRegion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ubicacion")
    private UbicacionEntity ubicacion;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "umbral_alerta")
    private Float umbralAlerta = 0.75F;

    @Column(name = "costo_cierre_mxn", precision = 15, scale = 2)
    private BigDecimal costoCierreMxn;

    @Column(name = "costo_apertura_mxn", precision = 15, scale = 2)
    private BigDecimal costoAperturaMxn;

    @Column(name = "costo_operacion_diaria_mxn", precision = 15, scale = 2)
    private BigDecimal costoOperacionDiariaMxn;

    @Column(name = "dias_reapertura_min")
    private Integer diasReaperturaMin;

    @Column(name = "dias_reapertura_max")
    private Integer diasReaperturaMax;

    @Column(name = "fuente_costos", length = 100)
    private String fuenteCostos;

    public PlantaEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }

    public Integer getIdRegion() { return idRegion; }
    public void setIdRegion(Integer idRegion) { this.idRegion = idRegion; }

    public UbicacionEntity getUbicacion() { return ubicacion; }
    public void setUbicacion(UbicacionEntity ubicacion) { this.ubicacion = ubicacion; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public Float getUmbralAlerta() { return umbralAlerta; }
    public void setUmbralAlerta(Float umbralAlerta) { this.umbralAlerta = umbralAlerta; }

    public BigDecimal getCostoCierreMxn() { return costoCierreMxn; }
    public void setCostoCierreMxn(BigDecimal costoCierreMxn) { this.costoCierreMxn = costoCierreMxn; }

    public BigDecimal getCostoAperturaMxn() { return costoAperturaMxn; }
    public void setCostoAperturaMxn(BigDecimal costoAperturaMxn) { this.costoAperturaMxn = costoAperturaMxn; }

    public BigDecimal getCostoOperacionDiariaMxn() { return costoOperacionDiariaMxn; }
    public void setCostoOperacionDiariaMxn(BigDecimal costoOperacionDiariaMxn) { this.costoOperacionDiariaMxn = costoOperacionDiariaMxn; }

    public Integer getDiasReaperturaMin() { return diasReaperturaMin; }
    public void setDiasReaperturaMin(Integer diasReaperturaMin) { this.diasReaperturaMin = diasReaperturaMin; }

    public Integer getDiasReaperturaMax() { return diasReaperturaMax; }
    public void setDiasReaperturaMax(Integer diasReaperturaMax) { this.diasReaperturaMax = diasReaperturaMax; }

    public String getFuenteCostos() { return fuenteCostos; }
    public void setFuenteCostos(String fuenteCostos) { this.fuenteCostos = fuenteCostos; }
}