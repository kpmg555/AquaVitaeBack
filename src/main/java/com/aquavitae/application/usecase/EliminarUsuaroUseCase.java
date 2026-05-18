package org.acme.application.usecase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.domain.models.Usuario;
import org.acme.domain.repository.FirebaseAuthPort;
import org.acme.domain.repository.UsuarioRepository;
import org.jboss.logging.Logger;
import java.util.NoSuchElementException;

@ApplicationScoped
public class EliminarUsuarioUseCase {

    private static final Logger LOG = Logger.getLogger(EliminarUsuarioUseCase.class);

    @Inject UsuarioRepository usuarioRepository;
    @Inject FirebaseAuthPort  firebaseAuthPort;

    public void execute(Integer idUsuario) {
        // 1. verificar que el usuario exista y esté activo
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NoSuchElementException(
                    "Usuario no encontrado: id=" + idUsuario));
        if (!usuario.isActivo()) {
            throw new IllegalStateException(
                "El usuario ya está desactivado: id=" + idUsuario);
        }

        // 2. deshabilitar en firebase
        try {
            firebaseAuthPort.deshabilitarUsuario(usuario.getUuid());
            LOG.infof("Usuario deshabilitado en Firebase: uuid=%s", usuario.getUuid());
        } catch (Exception e) {
            LOG.errorf("No se pudo deshabilitar en Firebase (uuid=%s): %s",
                usuario.getUuid(), e.getMessage());
        }

        // 3. Eliminado lógico en BD
        usuarioRepository.desactivar(idUsuario);
        LOG.infof("Usuario desactivado (eliminado lógico): id=%d, correo=%s",
            idUsuario, usuario.getCorreo());
    }
}