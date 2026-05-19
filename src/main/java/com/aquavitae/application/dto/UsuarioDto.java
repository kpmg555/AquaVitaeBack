package org.acme.application.dto;

public class UsuarioDto {
    private Integer id;
    private String  nombreCompleto;
    private String  correo;
    private String  nombreRol;
    private Integer idRol;
    private String  regionPlanta;   
    private boolean activo;
    private String  ultimoAcceso;   

    public UsuarioDto() {}

    public Integer getId(){ return id; }
    public void    setId(Integer id){ this.id = id; }

    public String  getNombreCompleto()                             { return nombreCompleto; }
    public void    setNombreCompleto(String nombreCompleto)        { this.nombreCompleto = nombreCompleto; }

    public String  getCorreo(){ return correo; }
    public void    setCorreo(String correo){ this.correo = correo; }

    public String  getNombreRol() { return nombreRol; }
    public void    setNombreRol(String nombreRol){ this.nombreRol = nombreRol; }

    public Integer getIdRol(){ return idRol; }
    public void    setIdRol(Integer idRol) { this.idRol = idRol; }

    public String  getRegionPlanta(){ return regionPlanta; }
    public void    setRegionPlanta(String regionPlanta) { this.regionPlanta = regionPlanta; }

    public boolean isActivo(){ return activo; }
    public void    setActivo(boolean activo) { this.activo = activo; }

    public String  getUltimoAcceso(){ return ultimoAcceso; }
    public void    setUltimoAcceso(String ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
}