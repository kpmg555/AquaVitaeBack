package org.acme.infrastructure.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Rol")
public class RolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 300)
    private String descripcion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "Rol_Permiso",
        joinColumns = @JoinColumn(name = "id_rol"),
        inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private Set<PermisoEntity> permisos = new HashSet<>();

    public RolEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre){ this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void etDescripcion(String descripcion){ this.descripcion = descripcion; }

    public Set<PermisoEntity> getPermisos()                       { return permisos; }
    public void setPermisos(Set<PermisoEntity> p)   { this.permisos = p; }
}