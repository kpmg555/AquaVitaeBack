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

@Path("/api/simulacion")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacionResource {

    @Inject
    ObtenerKpisSimulacionUseCase kpisUseCase;

    @Inject
    ObtenerProyeccionUseCase proyeccionUseCase;

    @Inject
    ObtenerRecuperacionUseCase recuperacionUseCase;

    @GET
    @Path("/kpis")
    public KpiSimulacionDto getKpis(@QueryParam("plantaId") Integer plantaId) {
        return kpisUseCase.execute(plantaId);
    }

    @GET
    @Path("/proyeccion")
    public ProyeccionDto getProyeccion(
            @QueryParam("plantaId") Integer plantaId,
            @QueryParam("dias") @DefaultValue("90") int dias) {
        return proyeccionUseCase.execute(plantaId, dias);
    }

    @GET
    @Path("/recuperacion")
    public RecuperacionDto getRecuperacion(
            @QueryParam("plantaId") Integer plantaId,
            @QueryParam("dias") @DefaultValue("90") int dias) {
        return recuperacionUseCase.execute(plantaId, dias);
    }
}
