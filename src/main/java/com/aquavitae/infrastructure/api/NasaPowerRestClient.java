package com.aquavitae.infrastructure.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/temporal/daily/point")
@RegisterRestClient(configKey = "nasa-power-api")
public interface NasaPowerRestClient {

    @GET
    NasaPowerResponse getDailyData(
            @QueryParam("parameters") String parameters,
            @QueryParam("community") String community,
            @QueryParam("longitude") double longitude,
            @QueryParam("latitude") double latitude,
            @QueryParam("start") String start,
            @QueryParam("end") String end,
            @QueryParam("format") String format
    );
}