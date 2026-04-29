package com.aquavitae.interfaces.rest;


import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dashboard")
@Produces(MediaType.APPLICATION_JSON)
public class DashboardResource {

    private final ObtenerResumenEjecutivoUseCase useCase;

    @Inject
    public DashboardResource(ObtenerResumenEjecutivoUseCase useCase) {
        this.useCase = useCase;

        @GET
        @Path("/resumen")
        @RolesAllowed({"Director", "Administrador"})
        public Response getResumenEjecutivo() {
            ResumenEjecutivoDto resumen = useCase.execute();
            return Response.ok(resumen).build();
        }
    }
}
