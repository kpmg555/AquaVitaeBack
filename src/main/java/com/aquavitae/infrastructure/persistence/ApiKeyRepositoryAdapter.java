package com.aquavitae.infrastructure.persistence;

import com.aquavitae.domain.models.ApiKeyInfo;
import com.aquavitae.domain.ports.ApiKeyRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ApiKeyRepositoryAdapter implements ApiKeyRepositoryPort {

    @Inject
    DataSource dataSource;

    @Override
    public List<ApiKeyInfo> findAll() {
        List<ApiKeyInfo> result = new ArrayList<>();

        String sql = """
                SELECT id, nombre, activa, expiracion, fecha_rotacion
                FROM API_Key
                ORDER BY nombre
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {
            while (rs.next()) {
                result.add(new ApiKeyInfo(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getBoolean("activa"),
                        rs.getString("expiracion"),
                        rs.getString("fecha_rotacion")
                ));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error loading API keys from database", e);
        }
    }

    @Override
    public ApiKeyInfo rotate(Integer id) {
        String updateSql = """
                UPDATE API_Key
                SET fecha_rotacion = CURRENT_TIMESTAMP,
                    activa = true
                WHERE id = ?
                """;

        String selectSql = """
                SELECT id, nombre, activa, expiracion, fecha_rotacion
                FROM API_Key
                WHERE id = ?
                """;

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                update.setInt(1, id);
                update.executeUpdate();
            }

            try (PreparedStatement select = connection.prepareStatement(selectSql)) {
                select.setInt(1, id);

                try (ResultSet rs = select.executeQuery()) {
                    if (rs.next()) {
                        return new ApiKeyInfo(
                                rs.getInt("id"),
                                rs.getString("nombre"),
                                rs.getBoolean("activa"),
                                rs.getString("expiracion"),
                                rs.getString("fecha_rotacion")
                        );
                    }
                }
            }

            throw new RuntimeException("API key not found: " + id);

        } catch (Exception e) {
            throw new RuntimeException("Error rotating API key", e);
        }
    }
}