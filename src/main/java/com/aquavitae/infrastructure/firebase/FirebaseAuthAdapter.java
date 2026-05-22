package com.aquavitae.infrastructure.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetUsersResult;
import com.google.firebase.auth.UidIdentifier;
import com.google.firebase.auth.UserIdentifier;
import com.google.firebase.auth.UserRecord;
import jakarta.enterprise.context.ApplicationScoped;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import org.jboss.logging.Logger;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class FirebaseAuthAdapter implements FirebaseAuthPort {

    private static final Logger LOG = Logger.getLogger(FirebaseAuthAdapter.class);
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("d MMM yyyy, h:mm a", new Locale("es", "MX"))
            .withZone(ZoneId.of("America/Mexico_City"));

    @Override
    public String crearUsuario(String correo, String contrasenaTemp, String nombreCompleto) {
        try {
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(correo)
                    .setPassword(contrasenaTemp)
                    .setDisplayName(nombreCompleto)
                    .setEmailVerified(false)
                    .setDisabled(false);
            UserRecord record = FirebaseAuth.getInstance().createUser(request);
            LOG.infof("Usuario creado en Firebase: uid=%s, email=%s", record.getUid(), correo);
            return record.getUid();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear usuario en Firebase: " + e.getMessage(), e);
        }
    }

    @Override
    public void deshabilitarUsuario(String uuid) {
        try {
            FirebaseAuth.getInstance().updateUser(new UserRecord.UpdateRequest(uuid).setDisabled(true));
            LOG.infof("Usuario deshabilitado en Firebase: uid=%s", uuid);
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

    @Override
    public Map<String, String> getUltimoAccesoBatch(List<String> uuids) {
        Map<String, String> resultado = new HashMap<>();
        if (uuids == null || uuids.isEmpty())
            return resultado;

        try {
            // CORRECCIÓN: usar List<UserIdentifier> en lugar de List<UidIdentifier>
            List<UserIdentifier> identifiers = uuids.stream()
                    .map(UidIdentifier::new)
                    .collect(Collectors.toList());

            GetUsersResult result = FirebaseAuth.getInstance().getUsers(identifiers);

            for (UserRecord record : result.getUsers()) {
                long lastSignIn = record.getUserMetadata().getLastSignInTimestamp();
                String formateado;
                if (lastSignIn == 0) {
                    formateado = "Nunca";
                } else {
                    formateado = FORMATTER.format(Instant.ofEpochMilli(lastSignIn))
                            .replace("a. m.", "a.m.")
                            .replace("p. m.", "p.m.");
                }
                resultado.put(record.getUid(), formateado);
            }

            for (String uid : uuids) {
                resultado.putIfAbsent(uid, "—");
            }

        } catch (Exception e) {
            LOG.errorf("Error al obtener último acceso de Firebase: %s", e.getMessage());
            uuids.forEach(uid -> resultado.put(uid, "—"));
        }

        return resultado;
    }
}