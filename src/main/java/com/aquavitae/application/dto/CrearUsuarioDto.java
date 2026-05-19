package org.acme.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CrearUsuarioDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;

    private String telefono;

    @NotNull(message = "El rol es obligatorio")
    private Integer idRol;

    @NotBlank(message = "La contraseña temporal es obligatoria")
    @Size(min = 12, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern.List({
        @Pattern(regexp = ".*[A-Z].*", message = "La contraseña debe contener al menos una letra mayúscula"),
        @Pattern(regexp = ".*[a-z].*", message = "La contraseña debe contener al menos una letra minúscula"),
        @Pattern(regexp = ".*\\d.*", message = "La contraseña debe contener al menos un número"),
        @Pattern(regexp = ".*[@$!%*?&].*", message = "La contraseña debe contener al menos un carácter especial (@$!%*?&)")
    })
    private String contrasenaTemp;

    @NotBlank(message = "Se debe seleccionar la planta o región de donde proviene el usuario")
    private String alcanceDatos;

    private Integer idPlanta;  
    private Integer idRegion;  

    public CrearUsuarioDto() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre){ this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido){ this.apellido = apellido; }

    public String getCorreo(){ return correo; }
    public void setCorreo(String correo){ this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono){ this.telefono = telefono; }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol){ this.idRol = idRol; }

    public String getContrasenaTemp(){ return contrasenaTemp; }
    public void setContrasenaTemp(String contrasenaTemp) { this.contrasenaTemp = contrasenaTemp; }

    public String getAlcanceDatos(){ return alcanceDatos; }
    public void setAlcanceDatos(String alcanceDatos){ this.alcanceDatos = alcanceDatos; }

    public Integer getIdPlanta(){ return idPlanta; }
    public void setIdPlanta(Integer idPlanta){ this.idPlanta = idPlanta; }

    public Integer getIdRegion() { return idRegion; }
    public void setIdRegion(Integer idRegion){ this.idRegion = idRegion; }
}