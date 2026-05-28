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

import java.util.List;
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
        when(rolRepository.findAll()).thenReturn(List.of(mock(), mock())); // 2 roles
        when(rolRepository.countTotalPermisos()).thenReturn(25);

        ResumenUsuariosDto result = useCase.obtenerResumen();

        assertEquals(8, result.getUsuariosActivos());
        assertEquals(12, result.getTotalUsuarios());
        assertEquals(2, result.getRolesDefinidos());
        assertEquals(25, result.getPermisosAsignados());
        assertEquals(0, result.getActividadReciente());
    }

    @Test
    void listar_withUsers_returnsPagedResponseWithAccessTimes() {
        // Preparar usuarios
        Usuario u1 = new Usuario();
        u1.setId(1);
        u1.setUuid("u1");
        u1.setNombre("Juan");
        u1.setApellido("Pérez");
        u1.setCorreo("juan@test.com");
        u1.setActivo(true);
        u1.setNombreRol("Admin");
        u1.setIdRol(1);
        u1.setNombreEmpresa("MiEmpresa");

        Usuario u2 = new Usuario();
        u2.setId(2);
        u2.setUuid("u2");
        u2.setNombre("Ana");
        u2.setApellido("López");
        u2.setCorreo("ana@test.com");
        u2.setActivo(true);
        u2.setNombreRol("Usuario");
        u2.setIdRol(2);
        u2.setNombreEmpresa("MiEmpresa");

        List<Usuario> usuarios = List.of(u1, u2);
        when(usuarioRepository.findActivosConDetalle(idEmpresa, 0, 10)).thenReturn(usuarios);
        when(usuarioRepository.countActivos(idEmpresa)).thenReturn(2L);

        Map<String, String> accesos = Map.of("u1", "2025-01-01", "u2", "Nunca");
        when(firebaseAuthPort.getUltimoAccesoBatch(List.of("u1", "u2"))).thenReturn(accesos);

        PagedResponse<UsuarioDto> response = useCase.listar(0, 10);

        assertEquals(2, response.getItems().size());
        assertEquals(2, response.getTotal());
        assertEquals(0, response.getPage());
        assertEquals(10, response.getSize());

        UsuarioDto dto1 = response.getItems().get(0);
        assertEquals("Juan Pérez", dto1.getNombreCompleto());
        assertEquals("juan@test.com", dto1.getCorreo());
        assertEquals("Admin", dto1.getNombreRol());
        assertEquals("2025-01-01", dto1.getUltimoAcceso());

        UsuarioDto dto2 = response.getItems().get(1);
        assertEquals("Nunca", dto2.getUltimoAcceso());
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
}