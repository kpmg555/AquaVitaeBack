package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.RolDto;
import com.aquavitae.domain.models.Rol;
import com.aquavitae.domain.repository.RolRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ApplicationScoped
public class ObtenerRolesUseCase {

    @Inject
    RolRepository rolRepository;

    public List<RolDto> listarTodos() {
        return rolRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RolDto obtenerConPermisos(Integer idRol) {
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado: " + idRol));
        return toDto(rol);
    }

    private RolDto toDto(Rol r) {
        RolDto dto = new RolDto();
        dto.setId(r.getId());
        dto.setNombre(r.getNombre());
        dto.setDescripcion(r.getDescripcion());
        dto.setPermisos(r.getPermisos());
        dto.setTotalPermisos(r.getTotalPermisos());
        return dto;
    }
}