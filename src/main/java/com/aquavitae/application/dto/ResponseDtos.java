package org.acme.application.dto;

import java.util.List;

class RolDto {
    private Integer id;
    private String nombre;
    private String descripcion;
    private List<String> permisos;    
    private int totalPermisos;

    public RolDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id){ this.id = id; }

    public String getNombre(){ return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion){ this.descripcion = descripcion; }

    public List<String> getPermisos(){ return permisos; }
    public void setPermisos(List<String> permisos) { this.permisos = permisos; }

    public int  getTotalPermisos() { return totalPermisos; }
    public void setTotalPermisos(int totalPermisos){ this.totalPermisos = totalPermisos; }
}

class ResumenUsuariosDto {
    private int usuariosActivos;
    private int totalUsuarios;
    private int rolesDefinidos;
    private int permisosAsignados;
    private int actividadReciente;

    public ResumenUsuariosDto() {}

    public int getUsuariosActivos() { return usuariosActivos; }
    public void setUsuariosActivos(int n){ this.usuariosActivos = n; }

    public int getTotalUsuarios(){ return totalUsuarios; }
    public void setTotalUsuarios(int n){ this.totalUsuarios = n; }

    public int getRolesDefinidos(){ return rolesDefinidos; }
    public void setRolesDefinidos(int n){ this.rolesDefinidos = n; }

    public int getPermisosAsignados(){ return permisosAsignados; }
    public void setPermisosAsignados(int n){ this.permisosAsignados = n; }

    public int getActividadReciente(){ return actividadReciente; }
    public void setActividadReciente(int n){ this.actividadReciente = n; }
}

class PagedResponse<T> {
    private List<T> items;
    private long total;     
    private int page;  
    private int size;   
    private int totalPages;

    public PagedResponse() {}

    public PagedResponse(List<T> items, long total, int page, int size) {
        this.items= items;
        this.total= total;
        this.page= page;
        this.size= size;
        this.totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
    }

    public List<T> getItems(){ return items; }
    public long getTotal(){ return total; }
    public int getPage(){ return page; }
    public int getSize(){ return size; }
    public int getTotalPages(){ return totalPages; }
}

class GenerarContrasenaResponseDto {
    private String contrasena;

    public GenerarContrasenaResponseDto() {}
    public GenerarContrasenaResponseDto(String contrasena) { this.contrasena = contrasena; }

    public String getContrasena(){ return contrasena; }
    public void   setContrasena(String c) { this.contrasena = c; }
}