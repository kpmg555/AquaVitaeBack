package org.acme.infrastructure.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Usuario")
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 36)
    private String uuid;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(name = "id_empresa")
    private Integer idEmpresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private RolEntity rol;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Usuario_Planta",
        joinColumns= @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_planta")
    )
    private Set<PlantaEntity> plantas = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Usuario_Region",
        joinColumns= @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_region")
    )
    private Set<RegionEntity> regiones = new HashSet<>();

    public UsuarioEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id){ this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNombre(){ return nombre; }
    public void setNombre(String nombre){ this.nombre = nombre; }

    public String getApellido(){ return apellido; }
    public void setApellido(String apellido){ this.apellido = apellido; }

    public String getCorreo(){ return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono(){ return telefono; }
    public void setTelefono(String telefono){ this.telefono = telefono; }

    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa){ this.idEmpresa = idEmpresa; }

    public RolEntity getRol(){ return rol; }
    public void setRol(RolEntity rol){ this.rol = rol; }

    public boolean isActivo(){ return activo; }
    public void setActivo(boolean activo){ this.activo = activo; }

    public Set<PlantaEntity> getPlantas(){ return plantas; }
    public void setPlantas(Set<PlantaEntity> p){ this.plantas = p; }
    
    public Set<RegionEntity> getRegiones(){ return regiones; }
    public void setRegiones(Set<RegionEntity> r){ this.regiones = r; }
}