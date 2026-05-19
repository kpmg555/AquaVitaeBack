package org.acme.domain.firebase;

public interface FirebaseAuthPort {

    String crearUsuario(String correo, String contrasenaTemp, String nombreCompleto);
    void deshabilitarUsuario(String uuid);
    String generarContrasenaAleatoria();
}