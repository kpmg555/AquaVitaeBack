package com.aquavitae.infrastructure.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.List;

@Path("/tools/GUI/webservices/")
@RegisterRestClient(configKey = "smn-api")
public interface SmnRestClient {

    @GET
    List<Municipio> getPronosticoPorMunicipios(
            @QueryParam("method") int method
    );
}