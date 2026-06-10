package com.aquavitae.interfaces.rest;

import com.aquavitae.application.dto.AlertaOperativaDto;
import com.aquavitae.application.dto.AlternativaUbicacionDto;
import com.aquavitae.application.dto.FactorEvaluacionDto;
import com.aquavitae.application.usecase.ObtenerAlertaOperativaUseCase;
import com.aquavitae.application.usecase.ObtenerAlternativasUseCase;
import com.aquavitae.application.usecase.ObtenerFactoresUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/alternativas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Alternativas de Ubicación", description = "Alertas operativas, ubicaciones alternativas y factores de evaluación por planta.")
public class AlternativasResource {

    private static final String MSG_PLANTA_ID_REQUERIDO = "plantaId requerido";

    @Inject ObtenerAlertaOperativaUseCase alertaUseCase;
    @Inject ObtenerAlternativasUseCase alternativasUseCase;
    @Inject ObtenerFactoresUseCase factoresUseCase;

    @GET
    @Path("/alerta")
    @Operation(
            summary = "Alerta operativa de la planta",
            description = "Devuelve la alerta operativa vigente para una planta. Responde 400 si no se envía plantaId."
    )
    public Response obtenerAlerta(
            @Parameter(description = "ID de la planta (requerido)", required = true)
            @QueryParam("plantaId") Integer plantaId) {
        if (plantaId == null) return Response.status(400).entity(MSG_PLANTA_ID_REQUERIDO).build();
        AlertaOperativaDto dto = alertaUseCase.execute(plantaId);
        return Response.ok(dto).build();
    }

    @GET
    @Path("/ubicaciones")
    @Operation(
            summary = "Ubicaciones alternativas",
            description = "Lista las ubicaciones alternativas sugeridas para una planta según su riesgo hídrico. Responde 400 si no se envía plantaId."
    )
    public Response obtenerAlternativas(
            @Parameter(description = "ID de la planta (requerido)", required = true)
            @QueryParam("plantaId") Integer plantaId) {
        if (plantaId == null) return Response.status(400).entity(MSG_PLANTA_ID_REQUERIDO).build();
        List<AlternativaUbicacionDto> lista = alternativasUseCase.execute(plantaId);
        return Response.ok(lista).build();
    }

    @GET
    @Path("/factores")
    @Operation(
            summary = "Factores de evaluación",
            description = "Devuelve los factores usados para evaluar y comparar las ubicaciones alternativas de una planta. Responde 400 si no se envía plantaId."
    )
    public Response obtenerFactores(
            @Parameter(description = "ID de la planta (requerido)", required = true)
            @QueryParam("plantaId") Integer plantaId) {
        if (plantaId == null) return Response.status(400).entity(MSG_PLANTA_ID_REQUERIDO).build();
        List<FactorEvaluacionDto> lista = factoresUseCase.execute(plantaId);
        return Response.ok(lista).build();
    }
}
