package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.dto.CrearUsuarioDto;
import com.aquavitae.application.dto.UsuarioDto;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.repository.UsuarioRepository;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CrearUsuarioUseCase {
    private static final Logger LOG = Logger.getLogger(CrearUsuarioUseCase.class);

    @Inject
    UsuarioRepository usuarioRepository;
    @Inject
    FirebaseAuthPort firebaseAuthPort;

    public UsuarioDto execute(CrearUsuarioDto dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException("Correo ya registrado: " + dto.getCorreo());
        }

        String nombreCompleto = dto.getNombre() + " " + dto.getApellido();
        String uuid;
        try {
            uuid = firebaseAuthPort.crearUsuario(dto.getCorreo(), dto.getContrasenaTemp(), nombreCompleto);
        } catch (Exception e) {
            throw new RuntimeException("Error en Firebase Auth: " + e.getMessage(), e);
        }

        Usuario usuario = new Usuario();
        usuario.setUuid(uuid);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setIdRol(dto.getIdRol());
        usuario.setIdEmpresa(dto.getIdEmpresa());
        usuario.setActivo(true);
        if (dto.getModulos() != null) {
            usuario.setModulosPersonalizados(dto.getModulos());
        }

        try {
            usuario = usuarioRepository.save(usuario);
        } catch (Exception e) {
            try {
                firebaseAuthPort.deshabilitarUsuario(uuid);
            } catch (Exception ex) {
                LOG.error(ex);
            }
            throw new RuntimeException("Error al guardar en BD", e);
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