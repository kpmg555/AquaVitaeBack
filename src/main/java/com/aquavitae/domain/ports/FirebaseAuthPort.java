package com.aquavitae.domain.ports;

import java.util.List;
import java.util.Map;

public interface FirebaseAuthPort {
    String crearUsuario(String correo, String contrasenaTemp, String nombreCompleto);

    void deshabilitarUsuario(String uuid);

    String generarContrasenaAleatoria();

    Map<String, String> getUltimoAccesoBatch(List<String> correos);

    void actualizarUsuario(String uuid, String correo, String nombreCompleto, String nuevaContrasena);
}