package com.aquavitae.infrastructure.persistence;

import com.aquavitae.domain.ports.AuditoriaWriterPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

@ApplicationScoped
public class AuditoriaWriterAdapter implements AuditoriaWriterPort {

    @Inject
    DataSource dataSource;

    @Override
    public void registrar(
            Integer idUsuario,
            String accion,
            String modulo,
            String entidad,
            String descripcion,
            String ip,
            String severidad,
            String valorAnterior,
            String valorNuevo
    ) {
        String sql = """
                INSERT INTO Auditoria (
                    id_usuario,
                    accion,
                    modulo,
                    entidad,
                    descripcion,
                    ip,
                    severidad,
                    valor_anterior,
                    valor_nuevo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, CAST(? AS JSON), CAST(? AS JSON))
                """;

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            if (idUsuario != null) {
                statement.setInt(1, idUsuario);
            } else {
                statement.setNull(1, Types.INTEGER);
            }

            statement.setString(2, accion);
            statement.setString(3, modulo);
            statement.setString(4, entidad);
            statement.setString(5, descripcion);
            statement.setString(6, ip != null && !ip.isBlank() ? ip : "0.0.0.0");
            statement.setString(7, severidad != null ? severidad : "INFO");
            statement.setString(8, valorAnterior != null ? valorAnterior : "{}");
            statement.setString(9, valorNuevo != null ? valorNuevo : "{}");

            statement.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Error registering audit event", e);
        }
    }
}