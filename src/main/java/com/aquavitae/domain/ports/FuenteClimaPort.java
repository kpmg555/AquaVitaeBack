package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.DatosClimaticos;

import java.util.List;

public interface FuenteClimaPort {
    List<DatosClimaticos> obtenerDatos(List<Planta> ubicaciones);
}
