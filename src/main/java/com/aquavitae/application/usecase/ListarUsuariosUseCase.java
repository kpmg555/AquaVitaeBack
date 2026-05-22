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

        Map<String, String> ultimosAccesos = firebaseAuthPort.getUltimoAccesoBatch(uuids);

        List<UsuarioDto> dtos = usuarios.stream()
                .map(u -> toDto(u, ultimosAccesos))
                .collect(Collectors.toList());

        return new PagedResponse<>(dtos, total, page, size);
    }

    private UsuarioDto toDto(Usuario u, Map<String, String> accesos) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(u.getId());
        dto.setNombreCompleto(u.getNombreCompleto());
        dto.setCorreo(u.getCorreo());
        dto.setNombreRol(u.getNombreRol());
        dto.setIdRol(u.getIdRol());
        dto.setActivo(u.isActivo());
        dto.setNombreEmpresa(u.getNombreEmpresa());
        dto.setUltimoAcceso(accesos.getOrDefault(u.getUuid(), "—"));
        return dto;
    }
}