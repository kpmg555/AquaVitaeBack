package com.aquavitae.interfaces.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.aquavitae.application.dto.AlertaDto;
import com.aquavitae.application.dto.DashboardRiesgoDto;
import com.aquavitae.application.dto.EvolucionRiesgoDto;
import com.aquavitae.application.usecase.ObtenerAlertasUseCase;
import com.aquavitae.application.usecase.ObtenerDashboardRiesgoUseCase;
import com.aquavitae.application.usecase.ObtenerEvolucionUseCase;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @Inject
    ObtenerDashboardRiesgoUseCase riesgoUseCase;

    @Inject
    ObtenerEvolucionUseCase evolucionUseCase;

    @Inject
    ObtenerAlertasUseCase alertasUseCase;

    @GET
    @Path("/riesgo")
    public Response getRiesgo() {
        DashboardRiesgoDto dto = riesgoUseCase.execute();
        return Response.ok(dto).build();
    }

    @GET
    @Path("/evolucion")
    public Response getEvolucion(@QueryParam("dias") @DefaultValue("7") int dias) {
        EvolucionRiesgoDto dto = evolucionUseCase.execute(dias);
        return Response.ok(dto).build();
    }

    @GET
    @Path("/alertas")
    public Response getAlertas(@QueryParam("limit") @DefaultValue("3") int limit) {
        return Response.ok(alertasUseCase.execute(limit)).build();
    }
}