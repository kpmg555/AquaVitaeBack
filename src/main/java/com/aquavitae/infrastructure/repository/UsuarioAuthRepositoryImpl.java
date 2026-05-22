package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import com.aquavitae.domain.repository.UsuarioAuthRepository;
import java.util.Optional;

@ApplicationScoped
public class UsuarioAuthRepositoryImpl implements UsuarioAuthRepository {

    @Inject
    EntityManager em;

    @Override
    public Optional<UsuarioAuthData> findByUid(String uid) {
        try {
            Object[] result = (Object[]) em.createNativeQuery("""
                    SELECT u.id_empresa, u.id_rol, u.nombre, u.apellido
                    FROM Usuario u
                    WHERE u.uuid = :uid AND u.activo = true
                    """)
                    .setParameter("uid", uid)
                    .getSingleResult();

            Integer idEmpresa = ((Number) result[0]).intValue();
            Integer idRol = ((Number) result[1]).intValue();
            String nombre = (String) result[2];
            String apellido = (String) result[3];
            return Optional.of(new UsuarioAuthData(idEmpresa, idRol, nombre, apellido));
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}