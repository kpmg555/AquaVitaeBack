package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.EditarUsuarioDto;
import com.aquavitae.application.dto.UsuarioDto;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.repository.UsuarioRepository;
import org.jboss.logging.Logger;

import java.util.NoSuchElementException;

@ApplicationScoped
public class EditarUsuarioUseCase {

    private static final Logger LOG = Logger.getLogger(EditarUsuarioUseCase.class);

    @Inject
    UsuarioRepository usuarioRepository;
    @Inject
    FirebaseAuthPort firebaseAuthPort;

    public UsuarioDto execute(Integer id, EditarUsuarioDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + id));

        if (!usuario.getCorreo().equals(dto.getCorreo())
                && usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("Correo ya registrado: " + dto.getCorreo());
        }

        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setActivo(dto.isActivo());
        usuario.setIdRol(dto.getIdRol());
        usuario.setAlcanceDatos(dto.getAlcanceDatos() != null ? dto.getAlcanceDatos() : "TODAS");
        usuario.setIdPlantaAsignada(dto.getIdPlantaAsignada());

        // Actualizar permisos solo si el frontend los envió
        if (dto.getModulos() != null) {
            usuario.setModulosPersonalizados(dto.getModulos());
        }

        usuario = usuarioRepository.save(usuario);

        if (usuario.getUuid() != null && !usuario.getUuid().isBlank()) {
            try {
                firebaseAuthPort.actualizarUsuario(
                        usuario.getUuid(),
                        dto.getCorreo(),
                        dto.getNombre() + " " + dto.getApellido(),
                        dto.getNuevaContrasena());
            } catch (Exception e) {
                LOG.warnf("Firebase sync failed for user %d: %s", id, e.getMessage());
            }
        }

        return toDto(usuario);
    }

    private UsuarioDto toDto(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(u.getId());
        dto.setNombreCompleto(u.getNombreCompleto());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setNombreUsuario(u.getNombreUsuario());
        dto.setTelefono(u.getTelefono());
        dto.setCorreo(u.getCorreo());
        dto.setIdRol(u.getIdRol());
        dto.setActivo(u.isActivo());
        dto.setNombreEmpresa(u.getNombreEmpresa());
        if (u.getModulosPersonalizados() != null) {
            dto.setModulosEfectivos(u.getModulosPersonalizados());
        }
        return dto;
    }
}
