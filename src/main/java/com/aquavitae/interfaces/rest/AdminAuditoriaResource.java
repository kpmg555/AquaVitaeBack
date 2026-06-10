package com.aquavitae.interfaces.rest;

import com.aquavitae.application.usecase.GetAuditoriaLogDetailUseCase;
import com.aquavitae.application.usecase.GetAuditoriaLogsUseCase;
import com.aquavitae.application.usecase.GetAuditoriaResumenUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/admin/auditoria")
@Tag(name = "Admin · Auditoría", description = "Bitácora de acciones del sistema: resumen, listado con filtros y detalle de cada registro.")
public class AdminAuditoriaResource {

    @Inject
    GetAuditoriaLogsUseCase getAuditoriaLogsUseCase;

    @Inject
    GetAuditoriaLogDetailUseCase getAuditoriaLogDetailUseCase;

    @Inject
    GetAuditoriaResumenUseCase getAuditoriaResumenUseCase;

    @GET
    @Path("/resumen")
    @Operation(
            summary = "Resumen de auditoría",
            description = "Devuelve un resumen agregado de la actividad registrada en la bitácora (totales por acción, módulo, severidad, etc.)."
    )
    public Response getResumen() {
        return Response.ok(getAuditoriaResumenUseCase.execute()).build();
    }

    @GET
    @Path("/logs")
    @Operation(
            summary = "Listar registros de auditoría",
            description = "Lista los registros de la bitácora con filtros opcionales por usuario, acción, módulo y severidad."
    )
    public Response getLogs(
            @Parameter(description = "Cantidad máxima de registros a devolver")
            @QueryParam("limit") Integer limit,
            @Parameter(description = "Filtrar por usuario que realizó la acción")
            @QueryParam("usuario") String usuario,
            @Parameter(description = "Filtrar por tipo de acción (ej. CREAR_USUARIO)")
            @QueryParam("accion") String accion,
            @Parameter(description = "Filtrar por módulo del sistema")
            @QueryParam("modulo") String modulo,
            @Parameter(description = "Filtrar por severidad (INFO, ALTA, etc.)")
            @QueryParam("severidad") String severidad
    ) {
        return Response.ok(
                getAuditoriaLogsUseCase.execute(
                        limit,
                        usuario,
                        accion,
                        modulo,
                        severidad
                )
        ).build();
    }

    @GET
    @Path("/logs/{id}")
    @Operation(
            summary = "Detalle de un registro de auditoría",
            description = "Devuelve el detalle completo de un registro de la bitácora, incluyendo valores anterior y nuevo."
    )
    public Response getLogDetail(
            @Parameter(description = "ID del registro de auditoría")
            @PathParam("id") Integer id) {
        return Response.ok(getAuditoriaLogDetailUseCase.execute(id)).build();
    }
}
