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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Dashboard de Riesgo", description = "Indicadores generales de riesgo hídrico, alertas recientes y su evolución.")
public class DashboardResource {

    @Inject
    ObtenerDashboardRiesgoUseCase dashboardUseCase;

    @Inject
    ObtenerAlertasUseCase alertasUseCase;

    @Inject
    ObtenerEvolucionUseCase evolucionUseCase;

    @GET
    @Path("/dashboard")
    @Operation(
            summary = "Resumen del dashboard de riesgo",
            description = "Devuelve los indicadores agregados de riesgo hídrico que alimentan la pantalla principal del dashboard."
    )
    public DashboardRiesgoDto getDashboard() {
        return dashboardUseCase.execute();
    }

    @GET
    @Path("/alertas")
    @Operation(
            summary = "Alertas recientes",
            description = "Lista las alertas más recientes generadas por el sistema, ordenadas de la más nueva a la más antigua."
    )
    public List<AlertaDto> getAlertas(
            @Parameter(description = "Cantidad máxima de alertas a devolver (por defecto 10)")
            @QueryParam("limit") @DefaultValue("10") int limit) {
        return alertasUseCase.execute(limit);
    }

    @GET
    @Path("/evolucion")
    @Operation(
            summary = "Evolución del riesgo",
            description = "Devuelve la serie histórica de riesgo hídrico de los últimos N días para graficar su tendencia."
    )
    public EvolucionRiesgoDto getEvolucion(
            @Parameter(description = "Número de días hacia atrás a considerar (por defecto 30)")
            @QueryParam("dias") @DefaultValue("30") int dias) {
        return evolucionUseCase.execute(dias);
    }
}
