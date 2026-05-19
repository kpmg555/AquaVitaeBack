package org.acme.infrastructure.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.domain.repository.FirebaseAuthPort;
import org.jboss.logging.Logger;
import java.security.SecureRandom;


@ApplicationScoped
public class FirebaseAuthAdapter implements FirebaseAuthPort {

    private static final Logger LOG = Logger.getLogger(FirebaseAuthAdapter.class);
    private static final String CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public String crearUsuario(String correo, String contrasenaTemp, String nombreCompleto) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(correo)
                .setPassword(contrasenaTemp)
                .setDisplayName(nombreCompleto)
                .setEmailVerified(false)
                .setDisabled(false);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
            LOG.infof("Usuario creado en Firebase Auth: uid=%s, email=%s",
                userRecord.getUid(), correo);
            return userRecord.getUid();

        } catch (Exception e) {
            throw new RuntimeException("Error al crear usuario en Firebase: " + e.getMessage(), e);
        }
    }

    @Override
    public void deshabilitarUsuario(String uuid) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uuid).setDisabled(true);
            FirebaseAuth.getInstance().updateUser(request);
            LOG.infof("Usuario deshabilitado en Firebase Auth: uid=%s", uuid);
        } catch (Exception e) {
            throw new RuntimeException("Error al deshabilitar usuario en Firebase: " + e.getMessage(), e);
        }
    }

    @Override
    public String generarContrasenaAleatoria() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}