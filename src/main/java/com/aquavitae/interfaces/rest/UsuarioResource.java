package com.aquavitae.interfaces.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.aquavitae.application.dto.*;
import com.aquavitae.application.usecase.*;
import com.aquavitae.domain.repository.EmpresaRepository;
import java.util.NoSuchElementException;

@Path("/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("Administrador")
public class UsuarioResource {

    @Inject
    ListarUsuariosUseCase listarUsuariosUseCase;
    @Inject
    CrearUsuarioUseCase crearUsuarioUseCase;
    @Inject
    EliminarUsuarioUseCase eliminarUsuarioUseCase;
    @Inject
    ObtenerRolesUseCase obtenerRolesUseCase;
    @Inject
    GenerarContrasenaUseCase generarContrasenaUseCase;
    @Inject
    EditarUsuarioUseCase editarUsuarioUseCase;
    @Inject
    EmpresaRepository empresaRepository;

    @GET
    @Path("/resumen")
    public Response getResumen() {
        return Response.ok(listarUsuariosUseCase.obtenerResumen()).build();
    }

    @GET
    public Response listar(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("5") int size) {
        return Response.ok(listarUsuariosUseCase.listar(page, size)).build();
    }

    @POST
    public Response crear(@Valid CrearUsuarioDto dto) {
        try {
            return Response.status(Response.Status.CREATED)
                    .entity(crearUsuarioUseCase.execute(dto)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error: " + e.getMessage())).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response editar(@PathParam("id") Integer id, @Valid EditarUsuarioDto dto) {
        try {
            return Response.ok(editarUsuarioUseCase.execute(id, dto)).build();
        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage())).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error: " + e.getMessage())).build();
        }
    }

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

    @GET
    @Path("/empresas")
    public Response getEmpresas() {
        return Response.ok(empresaRepository.listarTodas()).build();
    }

    public static class ErrorResponse {
        public final String mensaje;

        public ErrorResponse(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}