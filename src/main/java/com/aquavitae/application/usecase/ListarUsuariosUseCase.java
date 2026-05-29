package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.domain.models.PagedResponse;
import com.aquavitae.application.dto.ResumenUsuariosDto;
import com.aquavitae.application.dto.UsuarioDto;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.repository.RolRepository;
import com.aquavitae.domain.repository.UsuarioRepository;
import com.aquavitae.infrastructure.security.AuthContext;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class ListarUsuariosUseCase {

    @Inject
    UsuarioRepository usuarioRepository;
    @Inject
    RolRepository rolRepository;
    @Inject
    FirebaseAuthPort firebaseAuthPort;
    @Inject
    AuthContext authContext;

    public ResumenUsuariosDto obtenerResumen() {
        Integer idEmpresa = authContext.getUser().getIdEmpresa();
        ResumenUsuariosDto dto = new ResumenUsuariosDto();
        dto.setUsuariosActivos((int) usuarioRepository.countActivos(idEmpresa));
        dto.setTotalUsuarios((int) usuarioRepository.countTotal(idEmpresa));
        dto.setRolesDefinidos(rolRepository.findAll().size());
        dto.setPermisosAsignados(rolRepository.countTotalPermisos());
        dto.setActividadReciente(0);
        return dto;
    }

    public PagedResponse<UsuarioDto> listar(int page, int size) {
        Integer idEmpresa = authContext.getUser().getIdEmpresa();
        List<Usuario> usuarios = usuarioRepository.findActivosConDetalle(idEmpresa, page, size);
        long total = usuarioRepository.countActivos(idEmpresa);

        if (usuarios.isEmpty()) {
            return new PagedResponse<>(List.of(), total, page, size);
        }

        List<String> uuids = usuarios.stream()
                .map(Usuario::getUuid)
                .filter(uuid -> uuid != null && !uuid.isBlank())
                .collect(Collectors.toList());

        Map<String, String> accesosFirebase = firebaseAuthPort.getUltimoAccesoBatch(uuids);

        // Carga en batch los permisos personalizados de los usuarios de esta página
        List<Integer> ids = usuarios.stream().map(Usuario::getId).collect(Collectors.toList());
        Map<Integer, List<String>> modulosPorUsuario = usuarioRepository.findModulosPersonalizadosByIds(ids);

        List<UsuarioDto> dtos = usuarios.stream()
                .map(u -> toDto(u, accesosFirebase, modulosPorUsuario))
                .collect(Collectors.toList());

        return new PagedResponse<>(dtos, total, page, size);
    }

    private UsuarioDto toDto(Usuario u, Map<String, String> accesosFirebase,
                              Map<Integer, List<String>> modulosPorUsuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(u.getId());
        dto.setNombreCompleto(u.getNombreCompleto());
        dto.setCorreo(u.getCorreo());
        dto.setNombreRol(u.getNombreRol());
        dto.setIdRol(u.getIdRol());
        dto.setActivo(u.isActivo());
        dto.setNombreEmpresa(u.getNombreEmpresa());

        String firebaseFecha = accesosFirebase.getOrDefault(u.getUuid(), null);
        String fechaFinal;

        if (firebaseFecha != null && !firebaseFecha.equals("—") && !firebaseFecha.equals("Nunca")) {
            fechaFinal = firebaseFecha;
        } else if (u.getUltimoAcceso() != null) {
            fechaFinal = u.getUltimoAccesoFormateado();
        } else {
            fechaFinal = "—";
        }
        dto.setUltimoAcceso(fechaFinal);

        List<String> modulos = modulosPorUsuario.get(u.getId());
        if (modulos != null && !modulos.isEmpty()) {
            dto.setModulosEfectivos(modulos);
        }

        return dto;
    }
}
