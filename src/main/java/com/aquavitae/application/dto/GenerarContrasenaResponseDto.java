package com.aquavitae.application.dto;

public class GenerarContrasenaResponseDto {
    private String contrasena;

    public GenerarContrasenaResponseDto() {
    }

    public GenerarContrasenaResponseDto(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}