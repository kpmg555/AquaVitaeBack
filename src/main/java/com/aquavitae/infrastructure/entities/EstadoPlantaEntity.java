package com.aquavitae.infrastructure.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Estado_Planta")
public class EstadoPlantaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_planta", nullable = false)
    private PlantaEntity planta;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    private Double umbral;

    @Column(name = "proyeccion_nivel_mm")
    private Double proyeccionNivelMm;

    @Column(name = "nivel_agua", nullable = false)
    private Double nivelAgua;

    @Column(name = "indice_hidrico", nullable = false)
    private Double indiceHidrico;

    @Column(name = "evento_extremo")
    private Boolean eventoExtremo = false;

    @Column(name = "tipo_dato", length = 30)
    private String tipoDato = "historico";

    @Column(name = "unidad_nivel", length = 20)
    private String unidadNivel = "mm";

    @Column(length = 50)
    private String fuente = "OPEN_METEO";

    public EstadoPlantaEntity() {
        // JPA exige el constructor por default para que Hibernate pueda instanciar la entidad mediante reflexión.
        //  No se debe usar este constructor directamente; se llena mediante setters o un mapper.
    }

    public Integer getId() { return id; }

    public PlantaEntity getPlanta() { return planta; }
    public void setPlanta(PlantaEntity planta) { this.planta = planta; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Double getNivelAgua() { return nivelAgua; }
    public void setNivelAgua(Double nivelAgua) { this.nivelAgua = nivelAgua; }

    public Double getIndiceHidrico() { return indiceHidrico; }
    public void setIndiceHidrico(Double indiceHidrico) { this.indiceHidrico = indiceHidrico; }

    public String getTipoDato() { return tipoDato; }
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }
}