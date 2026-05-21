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

import java.util.List;

@Path("/api/alternativas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlternativasResource {

    @Inject ObtenerAlertaOperativaUseCase alertaUseCase;
    @Inject ObtenerAlternativasUseCase alternativasUseCase;
    @Inject ObtenerFactoresUseCase factoresUseCase;

    @GET
    @Path("/alerta")
    public Response obtenerAlerta(@QueryParam("plantaId") Integer plantaId) {
        if (plantaId == null) return Response.status(400).entity("plantaId requerido").build();
        AlertaOperativaDto dto = alertaUseCase.execute(plantaId);
        return Response.ok(dto).build();
    }

    @GET
    @Path("/ubicaciones")
    public Response obtenerAlternativas(@QueryParam("plantaId") Integer plantaId) {
        if (plantaId == null) return Response.status(400).entity("plantaId requerido").build();
        List<AlternativaUbicacionDto> lista = alternativasUseCase.execute(plantaId);
        return Response.ok(lista).build();
    }

    @GET
    @Path("/factores")
    public Response obtenerFactores(@QueryParam("plantaId") Integer plantaId) {
        if (plantaId == null) return Response.status(400).entity("plantaId requerido").build();
        List<FactorEvaluacionDto> lista = factoresUseCase.execute(plantaId);
        return Response.ok(lista).build();
    }
}
