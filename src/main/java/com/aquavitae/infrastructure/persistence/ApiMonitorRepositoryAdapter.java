package com.aquavitae.infrastructure.persistence;

import com.aquavitae.domain.models.ApiAlert;
import com.aquavitae.domain.models.ApiMonitorStatus;
import com.aquavitae.domain.ports.ApiMonitorRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ApiMonitorRepositoryAdapter implements ApiMonitorRepositoryPort {

    @Inject
    DataSource dataSource;

    @Override
    public List<ApiMonitorStatus> findStatus() {
        List<ApiMonitorStatus> result = new ArrayList<>();

        String sql = """
                SELECT 
                    nombre_api,
                    endpoint,
                    estado,
                    codigo_error,
                    mensaje,
                    ocurrencias
                FROM Monitor_API
                WHERE fecha IN (
                    SELECT MAX(fecha)
                    FROM Monitor_API
                    GROUP BY nombre_api
                )
                ORDER BY fecha DESC
                """;

        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            while (rs.next()) {
                result.add(new ApiMonitorStatus(
                        rs.getString("nombre_api"),
                        rs.getString("endpoint"),
                        rs.getString("estado"),
                        rs.getObject("codigo_error") != null ? rs.getInt("codigo_error") : null,
                        rs.getString("mensaje"),
                        rs.getInt("ocurrencias")
                ));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error loading API status from database", e);
        }
    }

    @Override
    public List<ApiAlert> findActiveAlerts() {
        List<ApiAlert> result = new ArrayList<>();

        String sql = """
                SELECT 
                    nombre_api,
                    endpoint,
                    codigo_error,
                    mensaje
                FROM Monitor_API
                WHERE resuelto = false
                  AND codigo_error IN (401, 404)
                ORDER BY fecha DESC
                """;

        try (
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)
        ) {
            while (rs.next()) {
                int code = rs.getInt("codigo_error");

                String severidad = code == 401 ? "CRITICA" : "ALTA";

                result.add(new ApiAlert(
                        rs.getString("nombre_api"),
                        rs.getString("endpoint"),
                        code,
                        rs.getString("mensaje"),
                        severidad
                ));
            }

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error loading API alerts from database", e);
        }
    }
}