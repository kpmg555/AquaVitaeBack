package com.aquavitae.domain.service;

public class ClasificarRiesgoService {

    public static String clasificar(float indice) {
        if (indice >= 0.70f) return "alto";
        if (indice >= 0.45f) return "medio";
        return "bajo";
    }

    public static String label(String riesgo) {
        return switch (riesgo) {
            case "alto"  -> "Alto";
            case "medio" -> "Medio";
            default      -> "Bajo";
        };
    }
}
