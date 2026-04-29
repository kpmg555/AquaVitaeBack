//  Llama a la Facade para obtener los datos raw
//   Usa el Strategy para clasificar cada planta
//   Usa el Builder para ensamblar el DTO de respuesta
//   El usecase NO conoce los detalles de implementación de estas tareas:
//   - No sabe cómo se consulta la BD (Facade lo oculta)
//   - No sabe cómo se calcula el riesgo (Strategy lo oculta)
//   - No sabe cómo se construye el DTO (Builder lo oculta)
//   Solo sabe QUÉ hacer, no CÓMO.

package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.ResumenEjecutivoDto;
import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.models.NivelRiesgo;
import com.aquavitae.domain.models.PlantaRiesgo;
import com.aquavitae.domain.service.ClasificadorRiesgoStrategy;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped

public class ObtenerResumenEjecutivoUseCase {

    // Inyectamos la Facade (puerto) y el Strategy
    @Inject RiesgoHidricoFacadePort facade;
    @Inject ClasificadorRiesgoStrategy clasificador;

    public ResumenEjecutivoDto execute() {
        //  Obtener datos via Facade
        List<PlantaRiesgo> plantas = facade.obtenerPlantasConRiesgo();
        long crisisActivas         = facade.contarCrisisActivas();
        List<AlertaDominio> alertas = facade.obtenerAlertasRecientes(3);
        Map<String, Float> evolucion = facade.obtenerEvolucion7Dias();

        // Clasificar plantas con Strategy
        long alto  = plantas.stream()
                .filter(p -> clasificador.clasificar(p.getIndiceHidrico()) == NivelRiesgo.ALTO)
                .count();
        long medio = plantas.stream()
                .filter(p -> clasificador.clasificar(p.getIndiceHidrico()) == NivelRiesgo.MEDIO)
                .count();
        long bajo  = plantas.stream()
                .filter(p -> clasificador.clasificar(p.getIndiceHidrico()) == NivelRiesgo.BAJO)
                .count();

        // Riesgo general = clasificación de la planta con mayor índice
        float maxIndice = (float) plantas.stream()
                .mapToDouble(p -> p.getIndiceHidrico() != null ? p.getIndiceHidrico() : 0.0)
                .max().orElse(0.0);
        NivelRiesgo riesgoGeneral = clasificador.clasificar(maxIndice);

        // Construir el DTO con Builder
        return ResumenEjecutivoDto.builder()
                .withKpis(buildKpis(riesgoGeneral, crisisActivas, alto, medio, bajo, plantas.size()))
                .withMapa(buildMapa(plantas))
                .withTabla(buildTabla(plantas))
                .withEvolucion(buildEvolucion(evolucion))
                .withAlertas(buildAlertas(alertas))
                .build();
    }

    private ResumenEjecutivoDto.KpisDto buildKpis(NivelRiesgo riesgoGeneral,
                                                  long crisis, long alto, long medio, long bajo, int total) {
        ResumenEjecutivoDto.KpisDto kpis = new ResumenEjecutivoDto.KpisDto();
        kpis.nivelRiesgoGeneral = riesgoGeneral.name().charAt(0)
                + riesgoGeneral.name().substring(1).toLowerCase();
        kpis.colorRiesgoGeneral = riesgoGeneral.getColor();
        kpis.descripcionRiesgo  = "El riesgo hídrico es " +
                kpis.nivelRiesgoGeneral.toLowerCase() + " en " + alto + " regiones";
        kpis.crisisActivas = crisis;
        kpis.plantasAlto   = alto;
        kpis.plantasMedio  = medio;
        kpis.plantasBajo   = bajo;
        kpis.totalPlantas  = total;
        return kpis;
    }

    private List<ResumenEjecutivoDto.PlantaMapaDto> buildMapa(List<PlantaRiesgo> plantas) {
        return plantas.stream().map(p -> {
            ResumenEjecutivoDto.PlantaMapaDto dto = new ResumenEjecutivoDto.PlantaMapaDto();
            dto.id = p.getId();
            dto.latitud = p.getLatitud();
            dto.longitud = p.getLongitud();
            NivelRiesgo nivel      = clasificador.clasificar(p.getIndiceHidrico());
            dto.nivelRiesgo  = nivel.name();
            dto.color  = nivel.getColor();
            dto.nombre = p.getNombre();
            dto.tieneAlertaCritica = p.getTieneAlertaCritica();
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ResumenEjecutivoDto.PlantaTablaDto> buildTabla(List<PlantaRiesgo> plantas) {
        return plantas.stream().map(p -> {
            ResumenEjecutivoDto.PlantaTablaDto dto = new ResumenEjecutivoDto.PlantaTablaDto();
            dto.nombre= p.getNombre();
            dto.ubicacion = p.getUbicacionNombre();
            NivelRiesgo nivel = clasificador.clasificar(p.getIndiceHidrico());
            dto.nivelRiesgo= nivel.name();
            dto.color = nivel.getColor();
            dto.tendencia= p.getTendencia();
            dto.nivelActualPct = p.getNivelActualPct();
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ResumenEjecutivoDto.EvolucionDto> buildEvolucion(Map<String, Float> evolucion) {
        return evolucion.entrySet().stream().map(e -> {
            ResumenEjecutivoDto.EvolucionDto dto = new ResumenEjecutivoDto.EvolucionDto();
            dto.fecha          = e.getKey();
            dto.indicePromedio = e.getValue();
            dto.umbralAlto = 0.75F;
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ResumenEjecutivoDto.AlertaDto> buildAlertas(List<AlertaDominio> alertas) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a",
                new Locale("es", "MX"));
        return alertas.stream().map(a -> {
            ResumenEjecutivoDto.AlertaDto dto = new ResumenEjecutivoDto.AlertaDto();
            dto.tipo = a.getTipo();
            dto.titulo = a.getTitulo();
            dto.descripcion = a.getDescripcion();
            dto.hora = a.getFecha() != null ? a.getFecha().format(fmt) : "";
            return dto;
        }).collect(Collectors.toList());
    }
}
