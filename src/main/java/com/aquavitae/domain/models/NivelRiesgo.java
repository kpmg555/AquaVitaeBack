//   Enum de dominio que representa los posibles estados de riesgo.
//   Al ser un enum en lugar de un String, el compilador  avisa  si
//  usas un valor inválido.
//   Es una regla de negocio pura: los niveles de riesgo existen
//   independientemente de cómo se almacenen o presenten.
// tiene las reglas del negocio para determinar el nivel de riesgo y
// la tendencia, lo que garantiza que estas reglas se apliquen de manera consistente en t
// oda la aplicación.


package com.aquavitae.domain.models;

public class NivelRiesgo {
        public static NivelRiesgo fromString(String nivel) {
            return switch (nivel.toUpperCase()) {
                case "ALTO"       -> ALTO;
                case "MEDIO"      -> MEDIO;
                case "BAJO"       -> BAJO;
                case "SIN_RIESGO" -> SIN_RIESGO;
                default           -> throw new IllegalArgumentException("Nivel de riesgo desconocido: " + nivel);
            };
        }

    public String getColor() {
        return switch (this) {
            case ALTO      -> "rojo";
            case MEDIO     -> "amarillo";
            case BAJO      -> "verde";
            case SIN_RIESGO -> "gris";
        };
    }

        // La tendencia: compara  el índice actual con el de hace 7 días.
        public static String calcularTendencia(Float indiceActual, Float indiceHace7Dias) {
            if (indiceActual == null || indiceHace7Dias == null) return "→";
            double delta = indiceActual - indiceHace7Dias;
            if (delta > 0.05)  return "↑";   // estrés aumentando = peor
            if (delta < -0.05) return "↓";   // estrés bajando = mejora
            return "→";
        }
}
