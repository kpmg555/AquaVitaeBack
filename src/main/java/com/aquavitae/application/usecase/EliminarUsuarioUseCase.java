package com.aquavitae.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import com.aquavitae.domain.repository.UsuarioRepository;
import org.jboss.logging.Logger;
import java.util.NoSuchElementException;

@ApplicationScoped
public class EliminarUsuarioUseCase {
    private static final Logger LOG = Logger.getLogger(EliminarUsuarioUseCase.class);

    @Inject
    UsuarioRepository usuarioRepository;
    @Inject
    FirebaseAuthPort firebaseAuthPort;

    public void execute(Integer idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: id=" + idUsuario));

        if (!usuario.isActivo()) {
            throw new IllegalStateException("El usuario ya está desactivado");
        }

        try {
            firebaseAuthPort.deshabilitarUsuario(usuario.getUuid());
            LOG.infof("Usuario deshabilitado en Firebase: %s", usuario.getUuid());
        } catch (Exception e) {
            LOG.errorf("No se pudo deshabilitar en Firebase: %s", e.getMessage());
        }

        usuarioRepository.desactivar(idUsuario);
        LOG.infof("Usuario desactivado lógicamente: id=%d", idUsuario);
    }
}