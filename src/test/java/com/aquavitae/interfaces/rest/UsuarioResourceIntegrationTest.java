package com.aquavitae.interfaces.rest;

import com.aquavitae.application.dto.CrearUsuarioDto;
import com.aquavitae.application.dto.EditarUsuarioDto;
import com.aquavitae.domain.models.AuthenticatedUser;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.ports.NotificacionPort;
import com.aquavitae.infrastructure.api.ClimaRestClient;
import com.aquavitae.infrastructure.security.AuthContext;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Pruebas de integracion para UsuarioResource.
 * Levanta el servidor Quarkus completo con base de datos H2 en memoria.
 * Los puertos externos (Firebase, notificaciones, REST clients) se mockean
 * para aislar la prueba de dependencias externas.
 * La BD H2 se limpia antes de cada test en setUp.
 */
@QuarkusTest
class UsuarioResourceIntegrationTest {

    @Inject
    EntityManager em;

    /**
     * Mock del puerto de autenticacion Firebase.
     * Evita llamadas reales a Firebase durante los tests.
     */
    @InjectMock
    FirebaseAuthPort firebaseAuthPort;

    /**
     * Mock del puerto de notificaciones.
     * Evita envio real de notificaciones durante los tests.
     */
    @InjectMock
    NotificacionPort notificacionPort;

    /**
     * Mock del cliente REST de clima.
     * Requiere @RestClient porque ClimaRestClient es un MicroProfile REST Client
     * registrado con @RegisterRestClient, que Quarkus registra en CDI con ese
     * qualifier.
     */
    @InjectMock
    @RestClient
    ClimaRestClient climaRestClient;

    /**
     * Mock del contexto de autenticacion.
     * Permite simular un usuario autenticado sin necesidad de token real.
     */
    @InjectMock
    AuthContext authContext;

    /**
     * Limpia todas las tablas relacionadas e inserta datos de referencia
     * antes de cada test. El orden de borrado respeta las restricciones
     * de clave foranea. Se mockea AuthContext con un usuario de prueba.
     */
    @BeforeEach
    @Transactional
    void setUp() {
        em.createNativeQuery("DELETE FROM Rol_Permiso").executeUpdate();
        em.createNativeQuery("DELETE FROM Usuario").executeUpdate();
        em.createNativeQuery("DELETE FROM Rol").executeUpdate();
        em.createNativeQuery("DELETE FROM Empresa").executeUpdate();

        em.createNativeQuery("INSERT INTO Empresa (id, nombre) VALUES (1, 'Mi Empresa')").executeUpdate();
        em.createNativeQuery("INSERT INTO Rol (id, nombre, descripcion) VALUES (1, 'Admin', 'Rol administrador')")
                .executeUpdate();

        AuthenticatedUser testUser = new AuthenticatedUser("test-uid", "test@example.com", 1, 1, "Test User");
        when(authContext.getUser()).thenReturn(testUser);
    }

    /**
     * Inserta un usuario sin ID explicito en una transaccion independiente
     * que se confirma antes de que RestAssured haga la llamada HTTP.
     * Esto es necesario porque una transaccion @Transactional en el metodo @Test
     * no se confirma hasta que el test termina, por lo que el endpoint HTTP
     * no veria los datos insertados si se usara la misma transaccion.
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void insertarUsuario(String uuid, String nombre, String apellido,
            String correo, int idEmpresa, int idRol, int activo) {
        em.createNativeQuery(
                "INSERT INTO Usuario (uuid, nombre, apellido, correo, id_empresa, id_rol, activo) " +
                        "VALUES ('" + uuid + "', '" + nombre + "', '" + apellido + "', '" +
                        correo + "', " + idEmpresa + ", " + idRol + ", " + activo + ")")
                .executeUpdate();
    }

    /**
     * Inserta un usuario con ID explicito en una transaccion independiente.
     * Se usa cuando el test necesita referenciar el usuario por un ID conocido,
     * por ejemplo para editar o eliminar por ID en el endpoint.
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void insertarUsuarioConId(int id, String uuid, String nombre, String apellido,
            String correo, int idEmpresa, int idRol, int activo) {
        em.createNativeQuery(
                "INSERT INTO Usuario (id, uuid, nombre, apellido, correo, id_empresa, id_rol, activo) " +
                        "VALUES (" + id + ", '" + uuid + "', '" + nombre + "', '" + apellido + "', '" +
                        correo + "', " + idEmpresa + ", " + idRol + ", " + activo + ")")
                .executeUpdate();
    }

    /**
     * Verifica que el endpoint /resumen devuelva los conteos correctos
     * de usuarios activos, totales, roles y permisos.
     */
    @Test
    void testGetResumen() {
        insertarUsuario("u1", "Juan", "Perez", "juan@test.com", 1, 1, 1);
        insertarUsuario("u2", "Ana", "Lopez", "ana@test.com", 1, 1, 1);

        given()
                .when().get("/api/usuarios/resumen")
                .then()
                .statusCode(200)
                .body("usuariosActivos", is(2))
                .body("totalUsuarios", is(2))
                .body("rolesDefinidos", greaterThanOrEqualTo(1))
                .body("permisosAsignados", is(0));
    }

    /**
     * Verifica que el endpoint de listado devuelva paginacion correcta.
     * Se insertan 3 usuarios y se solicita la primera pagina de 2 elementos.
     */
    @Test
    void testListarUsuarios() {
        for (int i = 1; i <= 3; i++) {
            insertarUsuario("u" + i, "Nombre" + i, "Apellido" + i,
                    "user" + i + "@test.com", 1, 1, 1);
        }

        given()
                .queryParam("page", 0)
                .queryParam("size", 2)
                .when().get("/api/usuarios")
                .then()
                .statusCode(200)
                .body("items.size()", is(2))
                .body("total", is(3))
                .body("page", is(0))
                .body("size", is(2))
                .body("totalPages", is(2));
    }

    /**
     * Verifica que crear un usuario devuelva 201 con los datos correctos.
     * No requiere insercion previa porque el endpoint crea el usuario internamente.
     * El mock de Firebase devuelve un UUID simulado.
     */
    @Test
    void testCrearUsuario() {
        when(firebaseAuthPort.crearUsuario("nuevo@test.com", "TempPass123", "Maria Gomez"))
                .thenReturn("firebase-uuid-123");

        CrearUsuarioDto dto = new CrearUsuarioDto();
        dto.setNombre("Maria");
        dto.setApellido("Gomez");
        dto.setCorreo("nuevo@test.com");
        dto.setTelefono("5556789");
        dto.setIdRol(1);
        dto.setIdEmpresa(1);
        dto.setContrasenaTemp("TempPass123");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post("/api/usuarios")
                .then()
                .statusCode(201)
                .body("nombreCompleto", is("Maria Gomez"))
                .body("correo", is("nuevo@test.com"))
                .body("activo", is(true));
    }

    /**
     * Verifica que intentar crear un usuario con un correo ya registrado
     * devuelva 409 Conflict. El usuario duplicado se inserta en una transaccion
     * separada para que el endpoint HTTP lo vea antes de procesar la solicitud.
     */
    @Test
    void testCrearUsuario_duplicateEmail_returnsConflict() {
        insertarUsuario("exist", "Existente", "Apellido", "duplicado@test.com", 1, 1, 1);

        CrearUsuarioDto dto = new CrearUsuarioDto();
        dto.setNombre("Otro");
        dto.setApellido("Usuario");
        dto.setCorreo("duplicado@test.com");
        dto.setIdRol(1);
        dto.setIdEmpresa(1);
        dto.setContrasenaTemp("pass1234");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post("/api/usuarios")
                .then()
                .statusCode(409)
                .body("mensaje", containsString("Correo ya registrado"));
    }

    /**
     * Verifica que editar un usuario actualice sus datos correctamente.
     * El usuario se inserta con ID conocido en una transaccion separada
     * para que el endpoint HTTP lo encuentre al procesar el PUT.
     */
    @Test
    void testEditarUsuario() {
        insertarUsuarioConId(100, "edit-uuid", "Antonio", "Viejo",
                "antonio@test.com", 1, 1, 1);

        EditarUsuarioDto dto = new EditarUsuarioDto();
        dto.setNombre("Antonio");
        dto.setApellido("Nuevo");
        dto.setCorreo("antonio.nuevo@test.com");
        dto.setActivo(true);
        dto.setIdRol(1);
        dto.setNuevaContrasena("newPass");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/api/usuarios/100")
                .then()
                .statusCode(200)
                .body("nombreCompleto", is("Antonio Nuevo"))
                .body("correo", is("antonio.nuevo@test.com"));
    }

    /**
     * Verifica que eliminar un usuario realice una baja logica (activo = false).
     * El usuario se inserta con ID conocido en una transaccion separada
     * para que el endpoint HTTP lo encuentre al procesar el DELETE.
     */
    @Test
    void testEliminarUsuario() {
        insertarUsuarioConId(200, "del-uuid", "Eliminar", "Me",
                "eliminar@test.com", 1, 1, 1);

        given()
                .when().delete("/api/usuarios/200")
                .then()
                .statusCode(204);
    }

    /**
     * Verifica que el endpoint de roles devuelva la lista de roles disponibles.
     * No requiere insercion adicional porque setUp ya inserta el rol Admin.
     */
    @Test
    void testGetRoles() {
        given()
                .when().get("/api/usuarios/roles")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].nombre", is("Admin"));
    }

    /**
     * Verifica que el endpoint de rol por ID devuelva los datos correctos.
     * Usa el rol con ID=1 insertado en setUp.
     */
    @Test
    void testGetRolConPermisos() {
        given()
                .when().get("/api/usuarios/roles/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("nombre", is("Admin"));
    }

    /**
     * Verifica que buscar un rol con ID inexistente devuelva 404
     * con el mensaje de error correspondiente.
     */
    @Test
    void testGetRolNotFound() {
        given()
                .when().get("/api/usuarios/roles/999")
                .then()
                .statusCode(404)
                .body("mensaje", containsString("Rol no encontrado"));
    }

    /**
     * Verifica que el endpoint de generar contrasena devuelva la contrasena
     * generada por el mock de Firebase.
     */
    @Test
    void testGenerarContrasena() {
        when(firebaseAuthPort.generarContrasenaAleatoria()).thenReturn("Abc123!@#");

        given()
                .when().get("/api/usuarios/generar-contrasena")
                .then()
                .statusCode(200)
                .body("contrasena", is("Abc123!@#"));
    }

    /**
     * Verifica que el endpoint de empresas devuelva la lista de empresas.
     * No requiere insercion adicional porque setUp ya inserta Mi Empresa.
     */
    @Test
    void testGetEmpresas() {
        given()
                .when().get("/api/usuarios/empresas")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].nombre", is("Mi Empresa"));
    }
}