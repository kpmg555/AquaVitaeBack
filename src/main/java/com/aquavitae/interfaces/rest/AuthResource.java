package com.aquavitae.interfaces.rest;

import com.aquavitae.application.usecase.VerifyFirebaseTokenUseCase;
import com.aquavitae.domain.models.AuthenticatedUser;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/auth")
public class AuthResource {

    @Inject
    VerifyFirebaseTokenUseCase verifyFirebaseTokenUseCase;

    @GET
    @Path("/me")
    public Response me(@HeaderParam("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Missing or invalid Authorization header"))
                    .build();
        }

        String token = authorizationHeader.substring("Bearer ".length());

        try {
            AuthenticatedUser user = verifyFirebaseTokenUseCase.execute(token);

            return Response.ok(Map.of(
                    "uid", user.getUid(),
                    "email", user.getEmail()
            )).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Invalid Firebase token"))
                    .build();
        }
    }
}