package com.aquavitae.interfaces.rest;

import com.aquavitae.application.dto.KpiSimulacionDto;
import com.aquavitae.application.dto.ProyeccionDto;
import com.aquavitae.application.dto.RecuperacionDto;
import com.aquavitae.application.usecase.ObtenerKpisSimulacionUseCase;
import com.aquavitae.application.usecase.ObtenerProyeccionUseCase;
import com.aquavitae.application.usecase.ObtenerRecuperacionUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/simulacion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Simulación Hídrica", description = "KPIs, proyección y escenarios de recuperación hídrica por planta.")
public class SimulacionResource {

    @Inject
    ObtenerKpisSimulacionUseCase kpisUseCase;

    @Inject
    ObtenerProyeccionUseCase proyeccionUseCase;

    @Inject
    ObtenerRecuperacionUseCase recuperacionUseCase;

    @GET
    @Path("/kpis")
    @Operation(
            summary = "KPIs de simulación",
            description = "Devuelve los indicadores clave (índice actual, días hasta umbral, probabilidad de evento y pérdida estimada) para una planta."
    )
    public KpiSimulacionDto getKpis(
            @Parameter(description = "ID de la planta a consultar")
            @QueryParam("plantaId") Integer plantaId) {
        return kpisUseCase.execute(plantaId);
    }

    @GET
    @Path("/proyeccion")
    @Operation(
            summary = "Proyección de estrés hídrico",
            description = "Devuelve la proyección del índice de estrés hídrico a N días para una planta, con bandas de confianza superior e inferior."
    )
    public ProyeccionDto getProyeccion(
            @Parameter(description = "ID de la planta a consultar")
            @QueryParam("plantaId") Integer plantaId,
            @Parameter(description = "Horizonte de la proyección en días (por defecto 90)")
            @QueryParam("dias") @DefaultValue("90") int dias) {
        return proyeccionUseCase.execute(plantaId, dias);
    }

    @GET
    @Path("/recuperacion")
    @Operation(
            summary = "Escenario de recuperación hídrica",
            description = "Devuelve el escenario de recuperación hídrica proyectado a N días para una planta."
    )
    public RecuperacionDto getRecuperacion(
            @Parameter(description = "ID de la planta a consultar")
            @QueryParam("plantaId") Integer plantaId,
            @Parameter(description = "Horizonte del escenario en días (por defecto 90)")
            @QueryParam("dias") @DefaultValue("90") int dias) {
        return recuperacionUseCase.execute(plantaId, dias);
    }
}
