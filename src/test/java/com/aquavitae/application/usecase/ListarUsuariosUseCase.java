package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.ResumenUsuariosDto;
import com.aquavitae.application.dto.UsuarioDto;
import com.aquavitae.domain.models.AuthenticatedUser;
import com.aquavitae.domain.models.PagedResponse;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.repository.RolRepository;
import com.aquavitae.domain.repository.UsuarioRepository;
import com.aquavitae.infrastructure.security.AuthContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarUsuariosUseCaseTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    RolRepository rolRepository;

    @Mock
    FirebaseAuthPort firebaseAuthPort;

    @Mock
    AuthContext authContext;

    @InjectMocks
    ListarUsuariosUseCase useCase;

    private final Integer idEmpresa = 5;
    private AuthenticatedUser authUser;

    @BeforeEach
    void setUp() {
        authUser = new AuthenticatedUser("uid", "user@example.com", idEmpresa, 1, "Test User");
        when(authContext.getUser()).thenReturn(authUser);
    }

    @Test
    void obtenerResumen_returnsCorrectSummary() {
        when(usuarioRepository.countActivos(idEmpresa)).thenReturn(8L);
        when(usuarioRepository.countTotal(idEmpresa)).thenReturn(12L);
        when(rolRepository.findAll()).thenReturn(List.of(mock(), mock()));
        when(rolRepository.countTotalPermisos()).thenReturn(25);

        ResumenUsuariosDto result = useCase.obtenerResumen();

        assertEquals(8, result.getUsuariosActivos());
        assertEquals(12, result.getTotalUsuarios());
        assertEquals(2, result.getRolesDefinidos());
        assertEquals(25, result.getPermisosAsignados());
        assertEquals(0, result.getActividadReciente());
    }

    @Test
    void listar_whenFirebaseHasValidDate_usesFirebaseDate() {
        Usuario u = createUsuario("u1", "Juan", "Pérez", "juan@test.com", null);
        when(usuarioRepository.findActivosConDetalle(idEmpresa, 0, 10)).thenReturn(List.of(u));
        when(usuarioRepository.countActivos(idEmpresa)).thenReturn(1L);

        Map<String, String> accesos = Map.of("u1", "15 may 2025, 2:30 p.m.");
        when(firebaseAuthPort.getUltimoAccesoBatch(List.of("u1"))).thenReturn(accesos);

        PagedResponse<UsuarioDto> response = useCase.listar(0, 10);

        assertEquals(1, response.getItems().size());
        UsuarioDto dto = response.getItems().get(0);
        assertEquals("15 may 2025, 2:30 p.m.", dto.getUltimoAcceso());
        // Verificar que NO se usó la fecha local
        verify(firebaseAuthPort).getUltimoAccesoBatch(List.of("u1"));
    }

    @Test
    void listar_whenFirebaseReturnsNunca_usesLocalDate() {
        LocalDateTime localDate = LocalDateTime.of(2025, 5, 10, 9, 0);
        String formattedLocal = formatLocalDateTime(localDate);

        Usuario u = createUsuario("u2", "Ana", "López", "ana@test.com", localDate);
        when(usuarioRepository.findActivosConDetalle(idEmpresa, 0, 10)).thenReturn(List.of(u));
        when(usuarioRepository.countActivos(idEmpresa)).thenReturn(1L);

        Map<String, String> accesos = Map.of("u2", "Nunca");
        when(firebaseAuthPort.getUltimoAccesoBatch(List.of("u2"))).thenReturn(accesos);

        PagedResponse<UsuarioDto> response = useCase.listar(0, 10);

        assertEquals(1, response.getItems().size());
        UsuarioDto dto = response.getItems().get(0);
        assertEquals(formattedLocal, dto.getUltimoAcceso());
    }

    @Test
    void listar_whenFirebaseReturnsDash_usesLocalDate() {
        LocalDateTime localDate = LocalDateTime.of(2025, 5, 10, 9, 0);
        String formattedLocal = formatLocalDateTime(localDate);

        Usuario u = createUsuario("u3", "Carlos", "Ruiz", "carlos@test.com", localDate);
        when(usuarioRepository.findActivosConDetalle(idEmpresa, 0, 10)).thenReturn(List.of(u));
        when(usuarioRepository.countActivos(idEmpresa)).thenReturn(1L);

        Map<String, String> accesos = Map.of("u3", "—");
        when(firebaseAuthPort.getUltimoAccesoBatch(List.of("u3"))).thenReturn(accesos);

        PagedResponse<UsuarioDto> response = useCase.listar(0, 10);

        assertEquals(1, response.getItems().size());
        UsuarioDto dto = response.getItems().get(0);
        assertEquals(formattedLocal, dto.getUltimoAcceso());
    }

    @Test
    void listar_whenFirebaseHasNoDataAndLocalDateIsNull_returnsDash() {
        Usuario u = createUsuario("u4", "Luis", "Mendoza", "luis@test.com", null);
        when(usuarioRepository.findActivosConDetalle(idEmpresa, 0, 10)).thenReturn(List.of(u));
        when(usuarioRepository.countActivos(idEmpresa)).thenReturn(1L);

        Map<String, String> accesos = Map.of(); // sin entrada para este uid
        when(firebaseAuthPort.getUltimoAccesoBatch(List.of("u4"))).thenReturn(accesos);

        PagedResponse<UsuarioDto> response = useCase.listar(0, 10);

        assertEquals(1, response.getItems().size());
        UsuarioDto dto = response.getItems().get(0);
        assertEquals("—", dto.getUltimoAcceso());
    }

    @Test
    void listar_emptyList_returnsEmptyPagedResponse() {
        when(usuarioRepository.findActivosConDetalle(idEmpresa, 0, 5)).thenReturn(List.of());
        when(usuarioRepository.countActivos(idEmpresa)).thenReturn(0L);

        PagedResponse<UsuarioDto> response = useCase.listar(0, 5);
        assertTrue(response.getItems().isEmpty());
        assertEquals(0, response.getTotal());
        verify(firebaseAuthPort, never()).getUltimoAccesoBatch(any());
    }

    // Helper methods
    private Usuario createUsuario(String uuid, String nombre, String apellido, String correo,
            LocalDateTime ultimoAcceso) {
        Usuario u = new Usuario();
        u.setId(1);
        u.setUuid(uuid);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setCorreo(correo);
        u.setActivo(true);
        u.setNombreRol("Admin");
        u.setIdRol(1);
        u.setNombreEmpresa("MiEmpresa");
        u.setUltimoAcceso(ultimoAcceso);
        return u;
    }

    private String formatLocalDateTime(LocalDateTime date) {
        if (date == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy, h:mm a", new Locale("es", "MX"));
        return date.format(formatter).replace("a. m.", "a.m.").replace("p. m.", "p.m.");
    }
}