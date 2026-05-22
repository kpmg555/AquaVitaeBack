package com.aquavitae.domain.repository;

import java.util.Optional;

public interface UsuarioAuthRepository {
    Optional<UsuarioAuthData> findByUid(String uid);

    class UsuarioAuthData {
        private final Integer idEmpresa;
        private final Integer idRol;
        private final String nombre;
        private final String apellido;

        public UsuarioAuthData(Integer idEmpresa, Integer idRol, String nombre, String apellido) {
            this.idEmpresa = idEmpresa;
            this.idRol = idRol;
            this.nombre = nombre;
            this.apellido = apellido;
        }

        public Integer getIdEmpresa() {
            return idEmpresa;
        }

        public Integer getIdRol() {
            return idRol;
        }

        public String getNombre() {
            return nombre;
        }

        public String getApellido() {
            return apellido;
        }
    }
}