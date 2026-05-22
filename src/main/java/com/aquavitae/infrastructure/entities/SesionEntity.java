package com.aquavitae.infrastructure.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Sesion")
public class SesionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "id_usuario")
    private Integer idUsuario;
    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;
    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
    private String ip;
    @Column(name = "user_agent")
    private String userAgent;
    private Boolean exitosa;
    // getters/setters
}