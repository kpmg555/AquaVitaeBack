package com.aquavitae.domain.models;

public class FactorEvaluacion {
    private String nombre;
    private String icono;   // water, money, clock, shield, truck
    private int puntos;     // 1-5
    private String color;

    public FactorEvaluacion(String nombre, String icono, int puntos, String color) {
        this.nombre = nombre;
        this.icono = icono;
        this.puntos = puntos;
        this.color = color;
    }

    public String getNombre() { return nombre; }
    public String getIcono() { return icono; }
    public int getPuntos() { return puntos; }
    public String getColor() { return color; }
}
