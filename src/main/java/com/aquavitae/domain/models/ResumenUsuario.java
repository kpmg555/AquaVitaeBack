package org.acme.domain.models;

public class ResumenUsuarios {
    private int usuariosActivos;
    private int totalUsuarios;
    private int rolesDefinidos;
    private int permisosAsignados;
    private int actividadReciente;  

    public ResumenUsuarios() {}

    public int getUsuariosActivos(){ return usuariosActivos; }
    public void setUsuariosActivos(int n){ this.usuariosActivos = n; }

    public int getTotalUsuarios(){ return totalUsuarios; }
    public void setTotalUsuarios(int n){ this.totalUsuarios = n; }

    public int getRolesDefinidos(){ return rolesDefinidos; }
    public void setRolesDefinidos(int n){ this.rolesDefinidos = n; }

    public int getPermisosAsignados() { return permisosAsignados; }
    public void setPermisosAsignados(int n){ this.permisosAsignados = n; }

    public int getActividadReciente(){ return actividadReciente; }
    public void setActividadReciente(int n){ this.actividadReciente = n; }
}