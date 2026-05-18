package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.application.dto.CrearUsuarioDto;
import org.acme.application.dto.UsuarioDto;
import org.acme.domain.models.Usuario;
import org.acme.domain.repository.FirebaseAuthPort;
import org.acme.domain.repository.UsuarioRepository;
import org.acme.infrastructure.security.AuthContext;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CrearUsuarioUseCase {

    private static final Logger LOG = Logger.getLogger(CrearUsuarioUseCase.class);

    @Inject UsuarioRepository usuarioRepository;
    @Inject FirebaseAuthPort  firebaseAuthPort;
    @Inject AuthContext       authContext;

    public UsuarioDto execute(CrearUsuarioDto dto) {
        // 1. validación básica (correo único)
        if (usuarioRepository.existsByCorreo(dto.getCorreo())) {
            throw new IllegalArgumentException(
                "Ya existe un usuario con el correo: " + dto.getCorreo());
        }

        // 2. crear en Firebase Auth
        String nombreCompleto = dto.getNombre() + " " + dto.getApellido();
        String uuid;
        try {
            uuid = firebaseAuthPort.crearUsuario(
                dto.getCorreo(), dto.getContrasenaTemp(), nombreCompleto);
            LOG.infof("Usuario creado en Firebase: %s → %s", dto.getCorreo(), uuid);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el usuario en Firebase Auth: " + e.getMessage(), e);
        }

        // 3. persistir en BD
        Usuario usuario = new Usuario();
        usuario.setUuid(uuid);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setTelefono(dto.getTelefono());
        usuario.setIdRol(dto.getIdRol());
        usuario.setIdEmpresa(authContext.getUser().getIdEmpresa());
        usuario.setActivo(true);

        try {
            usuario = usuarioRepository.save(usuario);
        } catch (Exception e) {
            // Si falla la persistencia en BD, deshabilitar el usuario en Firebase para evitar inconsistencias
            LOG.errorf("Error al persistir usuario en BD. Deshabilitando en Firebase: %s", uuid);
            try {
                firebaseAuthPort.deshabilitarUsuario(uuid);
            } catch (Exception fbEx) {
                LOG.errorf("No se pudo deshabilitar el usuario en Firebase: %s", fbEx.getMessage());
            }
            throw new RuntimeException("Error al guardar el usuario en la base de datos", e);
        }

        LOG.infof("Usuario creado exitosamente: id=%d, correo=%s", usuario.getId(), usuario.getCorreo());
        return toDto(usuario);
    }

    private UsuarioDto toDto(Usuario u) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(u.getId());
        dto.setNombreCompleto(u.getNombreCompleto());
        dto.setCorreo(u.getCorreo());
        dto.setIdRol(u.getIdRol());
        dto.setActivo(u.isActivo());
        return dto;
    }
}