

package com.aquavitae.application.dto;
import java.util.List;

public class ResumenEjecutivoDtoBuilder {

    private final ResumenEjecutivoDto dto = new ResumenEjecutivoDto();
    // Cada método recibe una sección, la asigna y devuelve el builder para encadenar llamadas

    public ResumenEjecutivoDtoBuilder withKpis(ResumenEjecutivoDto.KpisDto kpis) {
        dto.kpis = kpis;
        return this;
    }

    public ResumenEjecutivoDtoBuilder withMapa(List<ResumenEjecutivoDto.PlantaMapaDto> mapa) {
        dto.mapa = mapa;
        return this;
    }

    public ResumenEjecutivoDtoBuilder withTabla(List<ResumenEjecutivoDto.PlantaTablaDto> tabla) {
        dto.tabla = tabla;
        return this;
    }

    public ResumenEjecutivoDtoBuilder withEvolucion(List<ResumenEjecutivoDto.EvolucionDto> evolucion) {
        dto.evolucion7Dias = evolucion;
        return this;
    }

    public ResumenEjecutivoDtoBuilder withAlertas(List<ResumenEjecutivoDto.AlertaDto> alertas) {
        dto.alertasRecientes = alertas;
        return this;
    }

    // build() valida que todas las secciones estén presentes antes de devolver el objeto.
    public ResumenEjecutivoDto build() {
            if (dto.kpis == null) throw new IllegalStateException("Faltan KPIs");
            if (dto.mapa == null) throw new IllegalStateException("Falta el mapa");
            if (dto.tabla == null) throw new IllegalStateException("Falta la tabla");
            if (dto.evolucion7Dias == null) throw new IllegalStateException("Falta la evolución de 7 días");
            if (dto.alertasRecientes == null) throw new IllegalStateException("Faltan las alertas recientes");
        return dto;
    }
}
