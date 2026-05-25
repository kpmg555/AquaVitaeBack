package com.aquavitae.infrastructure.persistence;

import com.aquavitae.domain.models.AuthenticatedUser;
import com.aquavitae.domain.ports.AuthenticatedUserRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuthenticatedUserRepositoryAdapter implements AuthenticatedUserRepositoryPort {

    @Inject
    DataSource dataSource;

    @Override
    public Optional<AuthenticatedUser> findByUidOrEmail(String uid, String email) {
        String sql = """
                SELECT
                    u.uuid,
                    u.correo,
                    u.nombre,
                    u.apellido,
                    r.nombre AS rol,
                    p.clave AS permiso
                FROM Usuario u
                JOIN Rol r ON u.id_rol = r.id
                LEFT JOIN Rol_Permiso rp ON r.id = rp.id_rol
                LEFT JOIN Permiso p ON rp.id_permiso = p.id
                WHERE (u.uuid = ? OR u.correo = ?)
                  AND u.activo = TRUE
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, uid);
            statement.setString(2, email);

            try (ResultSet rs = statement.executeQuery()) {
                String dbUid = null;
                String dbEmail = null;
                String nombre = null;
                String apellido = null;
                String rol = null;
                List<String> permisos = new ArrayList<>();

                while (rs.next()) {
                    if (dbUid == null) {
                        dbUid = rs.getString("uuid");
                        dbEmail = rs.getString("correo");
                        nombre = rs.getString("nombre");
                        apellido = rs.getString("apellido");
                        rol = rs.getString("rol");
                    }

                    String permiso = rs.getString("permiso");
                    if (permiso != null && !permisos.contains(permiso)) {
                        permisos.add(permiso);
                    }
                }

                if (dbUid == null) {
                    return Optional.empty();
                }

                return Optional.of(new AuthenticatedUser(
                        dbUid,
                        dbEmail,
                        nombre,
                        apellido,
                        rol,
                        permisos
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading authenticated user from database", e);
        }
    }
}