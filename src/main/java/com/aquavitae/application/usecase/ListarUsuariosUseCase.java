package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.dto.PagedResponse;
import org.acme.application.dto.ResumenUsuariosDto;
import org.acme.application.dto.UsuarioDto;
import org.acme.domain.models.ResumenUsuarios;
import org.acme.domain.models.Usuario;
import org.acme.domain.repository.RolRepository;
import org.acme.domain.repository.UsuarioRepository;
import org.acme.infrastructure.security.AuthContext;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ListarUsuariosUseCase {

    @Inject UsuarioRepository usuarioRepository;
    @Inject RolRepository     rolRepository;
    @Inject AuthContext       authContext;

    public ResumenUsuariosDto obtenerResumen() {
        Integer idEmpresa = authContext.getUser().getIdEmpresa();

        ResumenUsuariosDto dto = new ResumenUsuariosDto();
        dto.setUsuariosActivos((int) usuarioRepository.countActivos(idEmpresa));
        dto.setTotalUsuarios((int) usuarioRepository.countTotal(idEmpresa));
        dto.setRolesDefinidos(rolRepository.findAll().size());
        dto.setPermisosAsignados(rolRepository.countTotalPermisos());
        dto.setActividadReciente(0); // TODO: implementar auditoría
        return dto;
    }

    public PagedResponse<UsuarioDto> listar(int page, int size) {
        Integer idEmpresa = authContext.getUser().getIdEmpresa();
        List<Usuario> usuarios = usuarioRepository.findActivos(idEmpresa, page, size);
        long total = usuarioRepository.countActivos(idEmpresa);

        List<UsuarioDto> dtos = usuarios.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(dtos, total, page, size);
    }

    private UsuarioDto toDto(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(u.getId());
        dto.setNombreCompleto(u.getNombreCompleto());
        dto.setCorreo(u.getCorreo());
        dto.setNombreRol(u.getNombreRol());
        dto.setIdRol(u.getIdRol());
        dto.setActivo(u.isActivo());
        dto.setRegionPlanta("Todas las regiones"); // TODO: leer alcance real
        dto.setUltimoAcceso("—");                 // TODO: leer de auditoría
        return dto;
    }
}