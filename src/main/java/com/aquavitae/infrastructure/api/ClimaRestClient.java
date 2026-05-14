package com.aquavitae.infrastructure.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import java.util.List;

@Path("/v1/forecast")
@RegisterRestClient(configKey = "open-meteo-api")
public interface ClimaRestClient {
    @GET
    List<ClimaticResponse> getForecast(
            @QueryParam("latitude") String latitude,
            @QueryParam("longitude") String longitude,
            @QueryParam("current") String current,
            @QueryParam("hourly") String hourly,
            @QueryParam("timezone") String timezone
    );
}