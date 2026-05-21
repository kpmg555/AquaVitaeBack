package com.aquavitae.domain.service;

import com.aquavitae.domain.models.EscenarioRecuperacion;
import com.aquavitae.domain.models.EscenarioRecuperacion.PuntoRecuperacion;

import java.util.ArrayList;
import java.util.List;

public class GenerarRecuperacionService {

    public static EscenarioRecuperacion generar(float peakValue, int dias) {
        List<PuntoRecuperacion> conIntervencion  = new ArrayList<>();
        List<PuntoRecuperacion> sinIntervencion  = new ArrayList<>();

        for (int d = 0; d <= dias; d += 2) {
            float t = d / (float) dias;

            float vCon = (float) (peakValue * Math.pow(1 - t, 1.6) + 18 * t * (1 - t) * 2);
            vCon = Math.max(15f, vCon);

            float vSin = (float) (peakValue - (peakValue - 60f) * Math.pow(t, 0.6));
            vSin = Math.max(50f, vSin);

            conIntervencion.add(new PuntoRecuperacion(d, vCon));
            sinIntervencion.add(new PuntoRecuperacion(d, vSin));
        }

        return new EscenarioRecuperacion(conIntervencion, sinIntervencion);
    }
}
