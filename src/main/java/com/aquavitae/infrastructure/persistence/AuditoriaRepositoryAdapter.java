package com.aquavitae.infrastructure.persistence;

import com.aquavitae.domain.models.AuditoriaLog;
import com.aquavitae.domain.ports.AuditoriaRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuditoriaRepositoryAdapter implements AuditoriaRepositoryPort {

    @Inject
    DataSource dataSource;

    @Override
    public List<AuditoriaLog> findRecentLogs(
            Integer limit,
            String usuario,
            String accion,
            String modulo,
            String severidad
    ) {
        List<AuditoriaLog> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT
                    a.id,
                    a.id_usuario,
                    TRIM(CONCAT(COALESCE(u.nombre, ''), ' ', COALESCE(u.apellido, ''))) AS usuario,
                    a.accion,
                    a.modulo,
                    a.entidad,
                    a.descripcion,
                    a.ip,
                    a.severidad,
                    CAST(a.valor_anterior AS CHAR) AS valor_anterior,
                    CAST(a.valor_nuevo AS CHAR) AS valor_nuevo,
                    a.fecha
                FROM Auditoria a
                LEFT JOIN Usuario u ON a.id_usuario = u.id
                WHERE 1 = 1
                """);

        List<Object> params = new ArrayList<>();

        if (usuario != null && !usuario.isBlank()) {
            sql.append("""
                    AND (
                        u.nombre LIKE ?
                        OR u.apellido LIKE ?
                        OR u.correo LIKE ?
                    )
                    """);
            String like = "%" + usuario + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }

        if (accion != null && !accion.isBlank()) {
            sql.append(" AND a.accion = ? ");
            params.add(accion);
        }

        if (modulo != null && !modulo.isBlank()) {
            sql.append(" AND a.modulo = ? ");
            params.add(modulo);
        }

        if (severidad != null && !severidad.isBlank()) {
            sql.append(" AND a.severidad = ? ");
            params.add(severidad);
        }

        sql.append(" ORDER BY a.fecha DESC LIMIT ? ");
        params.add(limit != null ? limit : 50);

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql.toString())
        ) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error loading audit logs", e);
        }
    }

    @Override
    public Optional<AuditoriaLog> findById(Integer id) {
        String sql = """
                SELECT
                    a.id,
                    a.id_usuario,
                    TRIM(CONCAT(COALESCE(u.nombre, ''), ' ', COALESCE(u.apellido, ''))) AS usuario,
                    a.accion,
                    a.modulo,
                    a.entidad,
                    a.descripcion,
                    a.ip,
                    a.severidad,
                    CAST(a.valor_anterior AS CHAR) AS valor_anterior,
                    CAST(a.valor_nuevo AS CHAR) AS valor_nuevo,
                    a.fecha
                FROM Auditoria a
                LEFT JOIN Usuario u ON a.id_usuario = u.id
                WHERE a.id = ?
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }

                return Optional.empty();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading audit log detail", e);
        }
    }

    @Override
    public int countLogsToday() {
        return count("""
                SELECT COUNT(*)
                FROM Auditoria
                WHERE DATE(fecha) = CURDATE()
                """);
    }

    @Override
    public int countCriticalChanges() {
        return count("""
                SELECT COUNT(*)
                FROM Auditoria
                WHERE severidad IN ('ALTA', 'CRITICA', 'CRÍTICA')
                """);
    }

    @Override
    public int countAuditedUsers() {
        return count("""
                SELECT COUNT(DISTINCT id_usuario)
                FROM Auditoria
                WHERE id_usuario IS NOT NULL
                """);
    }

    @Override
    public int countImmutableLogs() {
        return count("""
                SELECT COUNT(*)
                FROM Auditoria
                """);
    }

    private int count(String sql) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet rs = statement.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;

        } catch (Exception e) {
            throw new RuntimeException("Error counting audit data", e);
        }
    }

    private AuditoriaLog mapRow(ResultSet rs) throws Exception {
        Timestamp fecha = rs.getTimestamp("fecha");

        return new AuditoriaLog(
                rs.getInt("id"),
                rs.getObject("id_usuario") != null ? rs.getInt("id_usuario") : null,
                rs.getString("usuario"),
                rs.getString("accion"),
                rs.getString("modulo"),
                rs.getString("entidad"),
                rs.getString("descripcion"),
                rs.getString("ip"),
                rs.getString("severidad"),
                rs.getString("valor_anterior"),
                rs.getString("valor_nuevo"),
                fecha != null ? fecha.toLocalDateTime() : null
        );
    }
}