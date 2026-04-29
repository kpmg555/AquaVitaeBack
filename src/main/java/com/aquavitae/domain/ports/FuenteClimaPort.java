package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.DatosClimaticos;
import com.aquavitae.domain.models.Planta;

import java.util.List;

public interface FuenteClimaPort {
    List<DatosClimaticos> obtenerDatos(List<Planta> ubicaciones);
}
