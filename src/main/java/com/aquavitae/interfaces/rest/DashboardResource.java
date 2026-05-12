package com.aquavitae.interfaces.rest;

import com.aquavitae.application.dto.AlertaDto;
import com.aquavitae.application.dto.DashboardRiesgoDto;
import com.aquavitae.application.dto.EvolucionRiesgoDto;
import com.aquavitae.application.usecase.ObtenerAlertasUseCase;
import com.aquavitae.application.usecase.ObtenerDashboardRiesgoUseCase;
import com.aquavitae.application.usecase.ObtenerEvolucionUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashboardResource {

    @Inject
    ObtenerDashboardRiesgoUseCase dashboardUseCase;

    @Inject
    ObtenerAlertasUseCase alertasUseCase;

    @Inject
    ObtenerEvolucionUseCase evolucionUseCase;

    @GET
    @Path("/dashboard")
    public DashboardRiesgoDto getDashboard() {
        return dashboardUseCase.execute();
    }

    @GET
    @Path("/alertas")
    public List<AlertaDto> getAlertas(@QueryParam("limit") @DefaultValue("10") int limit) {
        return alertasUseCase.execute(limit);
    }

    @GET
    @Path("/evolucion")
    public EvolucionRiesgoDto getEvolucion(@QueryParam("dias") @DefaultValue("30") int dias) {
        return evolucionUseCase.execute(dias);
    }
}}