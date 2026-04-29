// Endpoint único:  GET /resumen-ejecutivo
// Solo el Director y el Administrador pueden acceder.

package com.aquavitae.interfaces.rest;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/resumen-ejecutivo")
@Produces(MediaType.APPLICATION_JSON)
public class ResumenEjecutivoResource {

    @Inject ObtenerResumenEjecutivoUseCase useCase;

    // Devuelve los 5 bloques de la vista en un solo JSON
    @GET
    public Response obtenerResumenEjecutivo() {
        ResumenEjecutivoDto resumen = useCase.execute();
        return Response.ok(resumen).build();
    }

}
