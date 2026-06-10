package com.aquavitae.interfaces.rest;

import com.aquavitae.application.usecase.VerifyFirebaseTokenUseCase;
import com.aquavitae.domain.models.AuthenticatedUser;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Map;

@Path("/auth")
@Tag(name = "Autenticación", description = "Validación de sesión y datos del usuario autenticado con Firebase.")
public class AuthResource {

    @Inject
    VerifyFirebaseTokenUseCase verifyFirebaseTokenUseCase;

    @GET
    @Path("/me")
    @Operation(
            summary = "Datos del usuario autenticado",
            description = "Valida el token de Firebase enviado en el header Authorization (Bearer) y devuelve el perfil del usuario: uid, email, nombre, rol y permisos. Responde 401 si el token falta o es inválido."
    )
    public Response me(
            @Parameter(description = "Token de Firebase en formato 'Bearer <token>'", required = true)
            @HeaderParam("Authorization") String authorizationHeader) {
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
                    "email", user.getEmail(),
                    "nombre", user.getNombre(),
                    "apellido", user.getApellido(),
                    "rol", user.getRol(),
                    "permisos", user.getPermisos()
            )).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
