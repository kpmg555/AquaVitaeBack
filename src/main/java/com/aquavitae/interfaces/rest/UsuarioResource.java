package org.acme.interfaces.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.application.dto.*;
import org.acme.application.usecase.*;
import java.util.NoSuchElementException;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("Administrador")
public class UsuarioResource {

    @Inject ListarUsuariosUseCase  listarUsuariosUseCase;
    @Inject CrearUsuarioUseCase    crearUsuarioUseCase;
    @Inject EliminarUsuarioUseCase eliminarUsuarioUseCase;
    @Inject ObtenerRolesUseCase    obtenerRolesUseCase;
    @Inject GenerarContrasenaUseCase generarContrasenaUseCase;

    @GET
    @Path("/resumen")
    public Response getResumen() {
        ResumenUsuariosDto resumen = listarUsuariosUseCase.obtenerResumen();
        return Response.ok(resumen).build();
    }

  import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@GET
public Response listar(
    @QueryParam("page") @DefaultValue("0") @Min(0) int page, 
    @QueryParam("size") @DefaultValue("5") @Min(1) @Max(100) int size
) { 
    PagedResponse<UsuarioDto> resultado = listarUsuariosUseCase.listar(page, size); 
    return Response.ok(resultado).build(); 
}

    @POST
    public Response crear(@Valid CrearUsuarioDto dto) {
        try {
            UsuarioDto creado = crearUsuarioUseCase.execute(dto);
            return Response.status(Response.Status.CREATED).entity(creado).build();
        } catch (IllegalArgumentException e) {
            // Correo duplicado o datos inválidos
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error al crear el usuario: " + e.getMessage())).build();
        }
    }
  
    // Eliminado LÓGICO: activo=false en BD y disabled=true en Firebase
    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            eliminarUsuarioUseCase.execute(id);
            return Response.noContent().build(); 
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @GET
    @Path("/roles")
    public Response getRoles() {
        return Response.ok(obtenerRolesUseCase.listarTodos()).build();
    }

    @GET
    @Path("/roles/{id}")
    public Response getRolConPermisos(@PathParam("id") Integer id) {
        try {
            return Response.ok(obtenerRolesUseCase.obtenerConPermisos(id)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @GET
    @Path("/generar-contrasena")
    public Response generarContrasena() {
        return Response.ok(generarContrasenaUseCase.execute()).build();
    }

    public static class ErrorResponse {
        public final String mensaje;
        public ErrorResponse(String mensaje) { this.mensaje = mensaje; }
    }
}