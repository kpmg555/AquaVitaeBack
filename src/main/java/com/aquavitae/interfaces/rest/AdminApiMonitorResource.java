package com.aquavitae.interfaces.rest;

import com.aquavitae.application.usecase.GetApiAlertsUseCase;
import com.aquavitae.application.usecase.GetApiKeysUseCase;
import com.aquavitae.application.usecase.GetApiStatusUseCase;
import com.aquavitae.application.usecase.RotateApiKeyUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/admin/apis")
public class AdminApiMonitorResource {

    @Inject
    GetApiStatusUseCase getApiStatusUseCase;

    @Inject
    GetApiAlertsUseCase getApiAlertsUseCase;

    @Inject
    GetApiKeysUseCase getApiKeysUseCase;

    @Inject
    RotateApiKeyUseCase rotateApiKeyUseCase;

    @GET
    @Path("/status")
    public Response getStatus() {
        return Response.ok(getApiStatusUseCase.execute()).build();
    }

    @GET
    @Path("/alerts")
    public Response getAlerts() {
        return Response.ok(getApiAlertsUseCase.execute()).build();
    }

    @GET
    @Path("/keys")
    public Response getApiKeys() {
        return Response.ok(getApiKeysUseCase.execute()).build();
    }

    @POST
    @Path("/keys/{id}/rotate")
    public Response rotateApiKey(@PathParam("id") Integer id) {
        return Response.ok(rotateApiKeyUseCase.execute(id)).build();
    }
}