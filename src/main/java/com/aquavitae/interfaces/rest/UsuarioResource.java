package com.aquavitae.interfaces.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.aquavitae.application.dto.*;
import com.aquavitae.application.usecase.*;
import com.aquavitae.domain.repository.EmpresaRepository;
import com.aquavitae.domain.ports.AuditoriaWriterPort;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;

@Path("/api/usuarios")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    private static final String MODULO_GESTION_USUARIOS = "Gestión Usuarios";

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

    @Inject
    AuditoriaWriterPort auditoriaWriterPort;

    @GET
    @Path("/resumen")
    public Response getResumen() {
        return Response.ok(listarUsuariosUseCase.obtenerResumen()).build();
    }

    @GET
    public Response listar(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("5") int size
    ) {
        return Response.ok(listarUsuariosUseCase.listar(page, size)).build();
    }

    @POST
    public Response crear(@Valid CrearUsuarioDto dto) {
        try {
            Object resultado = crearUsuarioUseCase.execute(dto);

            registrarAuditoriaSegura(
                    "CREAR_USUARIO",
                    MODULO_GESTION_USUARIOS,
                    "Usuario",
                    "Se creó un usuario desde el módulo de administración",
                    "INFO",
                    "{}",
                    toJsonFieldsSafe(dto)
            );

            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error: " + e.getMessage()))
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response editar(@PathParam("id") Integer id, @Valid EditarUsuarioDto dto) {
        try {
            Object resultado = editarUsuarioUseCase.execute(id, dto);

            registrarAuditoriaSegura(
                    "EDITAR_USUARIO",
                    MODULO_GESTION_USUARIOS,
                    "Usuario #" + id,
                    "Se editó un usuario desde el módulo de administración",
                    "INFO",
                    "{}",
                    toJsonFieldsSafe(dto)
            );

            return Response.ok(resultado).build();

        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error: " + e.getMessage()))
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response eliminar(@PathParam("id") Integer id) {
        try {
            eliminarUsuarioUseCase.execute(id);

            registrarAuditoriaSegura(
                    "ELIMINAR_USUARIO",
                    MODULO_GESTION_USUARIOS,
                    "Usuario #" + id,
                    "Se eliminó o desactivó un usuario desde el módulo de administración",
                    "ALTA",
                    "{\"id\":" + id + "}",
                    "{}"
            );

            return Response.noContent().build();

        } catch (NoSuchElementException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();

        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
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
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
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

    private void registrarAuditoriaSegura(
            String accion,
            String modulo,
            String entidad,
            String descripcion,
            String severidad,
            String valorAnterior,
            String valorNuevo
    ) {
        try {
            auditoriaWriterPort.registrar(
                    null,
                    accion,
                    modulo,
                    entidad,
                    descripcion,
                    "0.0.0.0",
                    severidad,
                    valorAnterior,
                    valorNuevo
            );
        } catch (Exception e) {
            System.err.println(">>> AUDITORIA ERROR: " + e.getMessage());
        }
    }

    private String toJsonFieldsSafe(Object object) {
        if (object == null) {
            return "{}";
        }

        StringBuilder json = new StringBuilder("{");
        Field[] fields = object.getClass().getDeclaredFields();
        boolean first = true;

        for (Field field : fields) {
            try {
                field.setAccessible(true);

                String fieldName = field.getName();

                if (fieldName.toLowerCase().contains("password")
                        || fieldName.toLowerCase().contains("contrasena")
                        || fieldName.toLowerCase().contains("contraseña")) {
                    continue;
                }

                Object value = field.get(object);

                if (!first) {
                    json.append(",");
                }

                json.append("\"")
                        .append(escapeJson(fieldName))
                        .append("\":");

                if (value == null) {
                    json.append("null");
                } else if (value instanceof Number || value instanceof Boolean) {
                    json.append(value);
                } else {
                    json.append("\"")
                            .append(escapeJson(String.valueOf(value)))
                            .append("\"");
                }

                first = false;

            } catch (Exception ignored) {
            }
        }

        json.append("}");
        return json.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    public static class ErrorResponse {
        public final String mensaje;

        public ErrorResponse(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}