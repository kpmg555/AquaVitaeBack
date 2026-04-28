package com.aquavitae.domain.models;

import java.util.UUID;

public class PlantaRiesgo {
        private Integer id;
        private String nombre;
        private String ubicacionNombre;  // "Monterrey, NL"
        private Float latitud;
        private Float longitud;
        private Float indiceHidrico;    // valor actual 0.0–1.0
        private Float indiceHace7Dias;  // para calcular tendencia
        private Boolean tieneAlertaCritica;

        public PlantaRiesgo() {}

        // ── Reglas de negocio ────────────────────────────────────────
        // Estas reglas viven aquí y NO en el usecase ni en el mapper,
        // porque son invariantes del dominio: siempre que exista una
        // PlantaRiesgo, estas reglas aplican.
        // ─────────────────────────────────────────────────────────────

        public NivelRiesgo getNivelRiesgo() {
            // Delegamos al Strategy — ver ClasificadorRiesgoHidrico.
            // El modelo NO conoce el Strategy directamente;
            // el Strategy recibe el modelo como parámetro.
            if (indiceHidrico == null) return NivelRiesgo.SIN_RIESGO;
            if (indiceHidrico >= 0.75) return NivelRiesgo.ALTO;
            if (indiceHidrico >= 0.45) return NivelRiesgo.MEDIO;
            return NivelRiesgo.BAJO;
        }

        public String getTendencia() {
            return NivelRiesgo.calcularTendencia(indiceHidrico, indiceHace7Dias);
        }

    // Nivel actual como porcentaje para la barra de la vista
    public Integer getNivelActualPct() {
        if (indiceHidrico == null) return 0;
        return (int) Math.round(indiceHidrico * 100);
    }

    //getters y setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacionNombre() { return ubicacionNombre; }
    public void setUbicacionNombre(String ubicacionNombre) { this.ubicacionNombre = ubicacionNombre; }

    public Float getLatitud() { return latitud; }
    public void setLatitud(Float latitud) { this.latitud = latitud; }

    public Float getLongitud() { return longitud; }
    public void setLongitud(Float longitud) { this.longitud = longitud; }

    public Float getIndiceHidrico() { return indiceHidrico; }
    public void setIndiceHidrico(Float indiceHidrico) { this.indiceHidrico = indiceHidrico; }

    public Float getIndiceHace7Dias() { return indiceHace7Dias; }
    public void setIndiceHace7Dias(Float indiceHace7Dias) { this.indiceHace7Dias = indiceHace7Dias; }

    public Boolean getTieneAlertaCritica() { return tieneAlertaCritica; }

    public void setTieneAlertaCritica(Boolean tieneAlertaCritica) { this.tieneAlertaCritica = tieneAlertaCritica; }

}
