//Dto compuesto que devuelve el  endpoint: Resumen Ejecutivo,
// con la lista de plantas de riesgo y el nivel de riesgo general.
//Usa patrón builder para construir el DTO a partir del modelo de dominio,
// aplicando las reglas de negocio necesarias para calcular el nivel de
// riesgo general a partir de las plantas individuales.
// Es un contrato de salida hacia el frontend.

package com.aquavitae.application.dto;
import java.util.List;

public class ResumenEjecutivoDto {
    protected KpisDto  kpis;
    protected List<PlantaMapaDto>  mapa;
    protected List<PlantaTablaDto> tabla;
    protected List<EvolucionDto>  evolucion7Dias;
    protected <AlertaDto> alertasRecientes;

    // Constructor privado
    protected ResumenEjecutivoDto() {}

    // Getters
    public KpisDto  getKpis() { return kpis; }
    public List<PlantaMapaDto> getMapa() { return mapa; }
    public List<PlantaTablaDto> getTabla()  { return tabla; }
    public List<EvolucionDto> getEvolucion7Dias()  { return evolucion7Dias; }
    public List<AlertaDto> getAlertasRecientes() { return alertasRecientes; }

    public static class KpisDto {
        public String nivelRiesgoGeneral;
        public String colorRiesgoGeneral;
        public String descripcionRiesgo;
        public long crisisActivas;
        public long plantasAlto;
        public long plantasMedio;
        public long plantasBajo;
        public long totalPlantas;
    }

    public static class PlantaMapaDto {
        public Integer id;
        public Float  latitud;
        public Float  longitud;
        public String  nivelRiesgo;
        public String  color;
        public String  nombre;
        public Boolean tieneAlertaCritica;
    }

    public static class PlantaTablaDto {
        public String  nombre;
        public String  ubicacion;
        public String  nivelRiesgo;
        public String  color;
        public String  tendencia;     // "↑" | "→" | "↓"
        public Integer nivelActualPct;
    }

    public static class EvolucionDto {
        public String fecha;
        public Float indicePromedio;
        public Float umbralAlto;
    }

    public static class AlertaDto {
        public String tipo;
        public String titulo;
        public String descripcion;
        public String hora;
    }

    public static ResumenEjecutivoDtoBuilder builder() {
        return new ResumenEjecutivoDtoBuilder();
    }

}
