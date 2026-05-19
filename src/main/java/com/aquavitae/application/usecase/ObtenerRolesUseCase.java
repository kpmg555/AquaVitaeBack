package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.dto.GenerarContrasenaResponseDto;
import org.acme.application.dto.RolDto;
import org.acme.domain.models.Rol;
import org.acme.domain.repository.FirebaseAuthPort;
import org.acme.domain.repository.RolRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ApplicationScoped
public class ObtenerRolesUseCase {

    @Inject RolRepository rolRepository;
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

@ApplicationScoped
class GenerarContrasenaUseCase {

    @Inject FirebaseAuthPort firebaseAuthPort;

    public GenerarContrasenaResponseDto execute() {
        String contrasena = firebaseAuthPort.generarContrasenaAleatoria();
        return new GenerarContrasenaResponseDto(contrasena);
    }
}