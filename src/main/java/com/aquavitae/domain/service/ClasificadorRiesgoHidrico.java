// Implementación concreta de Strategy.

package com.aquavitae.domain.service;
import com.aquavitae.domain.models.NivelRiesgo;
import jakarta.enterprise.context.ApplicationScoped;
import com.aquavitae.domain.models.NivelRiesgo;

@ApplicationScoped
public class ClasificadorRiesgoHidrico implements ClasificadorRiesgoStrategy {

        // Umbrales de clasificación de riesgo hídrico
        // indiceHidrico:  0.0 (sin estrés) a 1.0 (estrés máximo)
        private static final float UMBRAL_ALTO = 0.75f;
        private static final float UMBRAL_MEDIO = 0.45f;

        @Override
        public NivelRiesgo clasificar(Float indiceHidrico) {
            if (indiceHidrico == null) return NivelRiesgo.SIN_RIESGO;
            if (indiceHidrico >= UMBRAL_ALTO) return NivelRiesgo.ALTO;
            if (indiceHidrico >= UMBRAL_MEDIO) return NivelRiesgo.MEDIO;
            return NivelRiesgo.BAJO;
        }
}
