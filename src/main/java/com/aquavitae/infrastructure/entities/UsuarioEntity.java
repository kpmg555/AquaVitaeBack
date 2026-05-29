package com.aquavitae.infrastructure.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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
    @Column(name = "nombre_usuario", length = 50, unique = true)
    private String nombreUsuario;
    @Column(nullable = false, unique = true, length = 100)
    private String correo;
    @Column(length = 20)
    private String telefono;
    @Column(name = "id_empresa", nullable = false)
    private Integer idEmpresa;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private RolEntity rol;
    @Column(nullable = false)
    private boolean activo = true;
    @Column(name = "alcance_datos", length = 20)
    private String alcanceDatos = "TODAS";
    @Column(name = "id_planta_asignada")
    private Integer idPlantaAsignada;
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    // Permisos personalizados del usuario (sobrescriben los del rol)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "Usuario_Permiso",
        joinColumns = @JoinColumn(name = "id_usuario"),
        inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private Set<PermisoEntity> permisosPersonalizados = new HashSet<>();

    // getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }

    public RolEntity getRol() { return rol; }
    public void setRol(RolEntity rol) { this.rol = rol; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getAlcanceDatos() { return alcanceDatos; }
    public void setAlcanceDatos(String alcanceDatos) { this.alcanceDatos = alcanceDatos; }

    public Integer getIdPlantaAsignada() { return idPlantaAsignada; }
    public void setIdPlantaAsignada(Integer idPlantaAsignada) { this.idPlantaAsignada = idPlantaAsignada; }

    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public Set<PermisoEntity> getPermisosPersonalizados() { return permisosPersonalizados; }
    public void setPermisosPersonalizados(Set<PermisoEntity> permisosPersonalizados) {
        this.permisosPersonalizados = permisosPersonalizados;
    }
}
