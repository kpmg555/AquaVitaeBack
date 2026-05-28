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

@Path("/admin/auditoria")
public class AdminAuditoriaResource {

    @Inject
    GetAuditoriaLogsUseCase getAuditoriaLogsUseCase;

    @Inject
    GetAuditoriaLogDetailUseCase getAuditoriaLogDetailUseCase;

    @Inject
    GetAuditoriaResumenUseCase getAuditoriaResumenUseCase;

    @GET
    @Path("/resumen")
    public Response getResumen() {
        return Response.ok(getAuditoriaResumenUseCase.execute()).build();
    }

    @GET
    @Path("/logs")
    public Response getLogs(
            @QueryParam("limit") Integer limit,
            @QueryParam("usuario") String usuario,
            @QueryParam("accion") String accion,
            @QueryParam("modulo") String modulo,
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
    public Response getLogDetail(@PathParam("id") Integer id) {
        return Response.ok(getAuditoriaLogDetailUseCase.execute(id)).build();
    }
}