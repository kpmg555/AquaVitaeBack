package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.DatosClimaticos;
import com.aquavitae.domain.models.UbicacionClima;
import java.util.List;

public interface FuenteClimaPort {
    List<DatosClimaticos> obtenerDatos(List<UbicacionClima> ubicaciones);
    
    String getNombre();
}