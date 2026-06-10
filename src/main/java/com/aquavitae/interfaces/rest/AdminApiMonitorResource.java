package com.aquavitae.interfaces.rest;

import com.aquavitae.application.usecase.GetApiAlertsUseCase;
import com.aquavitae.application.usecase.CheckExternalApisUseCase;
import com.aquavitae.application.usecase.GetApiKeysUseCase;
import com.aquavitae.application.usecase.GetApiStatusUseCase;
import com.aquavitae.application.usecase.RotateApiKeyUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/admin/apis")
@Tag(name = "Admin · Monitoreo de APIs", description = "Estado, alertas y llaves de las APIs externas de clima; chequeo manual y rotación de llaves.")
public class AdminApiMonitorResource {

    @Inject
    GetApiStatusUseCase getApiStatusUseCase;

    @Inject
    GetApiAlertsUseCase getApiAlertsUseCase;

    @Inject
    GetApiKeysUseCase getApiKeysUseCase;

    @Inject
    RotateApiKeyUseCase rotateApiKeyUseCase;

    @Inject
    CheckExternalApisUseCase checkExternalApisUseCase;

    @GET
    @Path("/status")
    @Operation(
            summary = "Estado de las APIs externas",
            description = "Devuelve el estado actual (disponible/caída) de cada API externa de clima monitoreada."
    )
    public Response getStatus() {
        return Response.ok(getApiStatusUseCase.execute()).build();
    }

    @GET
    @Path("/alerts")
    @Operation(
            summary = "Alertas de APIs",
            description = "Lista las alertas registradas por fallos o degradación de las APIs externas."
    )
    public Response getAlerts() {
        return Response.ok(getApiAlertsUseCase.execute()).build();
    }

    @GET
    @Path("/keys")
    @Operation(
            summary = "Llaves de API",
            description = "Devuelve las llaves de API configuradas para los servicios externos (sin exponer el valor secreto completo)."
    )
    public Response getApiKeys() {
        return Response.ok(getApiKeysUseCase.execute()).build();
    }

    @POST
    @Path("/check")
    @Operation(
            summary = "Chequeo manual de APIs",
            description = "Dispara una verificación inmediata del estado de todas las APIs externas, en vez de esperar al chequeo programado."
    )
    public Response checkApis() {
        checkExternalApisUseCase.execute();
        return Response.ok().build();
    }

    @POST
    @Path("/keys/{id}/rotate")
    @Operation(
            summary = "Rotar llave de API",
            description = "Genera una nueva llave para la API indicada y deja la anterior inválida."
    )
    public Response rotateApiKey(
            @Parameter(description = "ID de la llave de API a rotar")
            @PathParam("id") Integer id) {
        return Response.ok(rotateApiKeyUseCase.execute(id)).build();
    }
}
