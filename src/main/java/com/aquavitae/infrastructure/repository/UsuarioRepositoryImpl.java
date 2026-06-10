package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.repository.UsuarioRepository;
import com.aquavitae.infrastructure.entities.PermisoEntity;
import com.aquavitae.infrastructure.entities.RolEntity;
import com.aquavitae.infrastructure.entities.UsuarioEntity;
import com.aquavitae.infrastructure.mapper.UsuarioMapper;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class UsuarioRepositoryImpl implements UsuarioRepository {

    @Inject
    EntityManager em;

    private static final String PARAM_ID_EMPRESA = "idEmpresa";

    private static final String SQL_USUARIOS = """
            SELECT
                u.id, u.uuid, u.nombre, u.apellido, u.correo, u.telefono,
                u.id_empresa, e.nombre AS nombre_empresa,
                u.id_rol, r.nombre AS nombre_rol,
                u.activo,
                MAX(s.fecha_inicio) AS ultimo_acceso
            FROM Usuario u
            JOIN Empresa e ON e.id = u.id_empresa
            JOIN Rol r ON r.id = u.id_rol
            LEFT JOIN Sesion s ON s.id_usuario = u.id AND s.exitosa = true
            WHERE u.id_empresa = :idEmpresa AND u.activo = true
            GROUP BY u.id, u.uuid, u.nombre, u.apellido, u.correo, u.telefono,
                     u.id_empresa, e.nombre, u.id_rol, r.nombre, u.activo
            ORDER BY u.nombre, u.apellido
            LIMIT :size OFFSET :offset
            """;

    private static final String SQL_COUNT = "SELECT COUNT(*) FROM Usuario u WHERE u.id_empresa = :idEmpresa AND u.activo = true";
    private static final String SQL_COUNT_TOTAL = "SELECT COUNT(*) FROM Usuario u WHERE u.id_empresa = :idEmpresa";

    @Override
    @SuppressWarnings("unchecked")
    public List<Usuario> findActivosConDetalle(Integer idEmpresa, int page, int size) {
        List<Object[]> rows = em.createNativeQuery(SQL_USUARIOS)
                .setParameter(PARAM_ID_EMPRESA, idEmpresa)
                .setParameter("size", size)
                .setParameter("offset", page * size)
                .getResultList();
        return rows.stream().map(this::mapRow).collect(Collectors.toList());
    }

    @Override
    public long countActivos(Integer idEmpresa) {
        return ((Number) em.createNativeQuery(SQL_COUNT).setParameter(PARAM_ID_EMPRESA, idEmpresa).getSingleResult())
                .longValue();
    }

    @Override
    public long countTotal(Integer idEmpresa) {
        return ((Number) em.createNativeQuery(SQL_COUNT_TOTAL).setParameter(PARAM_ID_EMPRESA, idEmpresa).getSingleResult())
                .longValue();
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        // JOIN FETCH para cargar permisosPersonalizados en la misma query
        return em.createQuery(
                "SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.permisosPersonalizados WHERE u.id = :id",
                UsuarioEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByUuid(String uuid) {
        return em.createQuery("SELECT u FROM UsuarioEntity u WHERE u.uuid = :uuid", UsuarioEntity.class)
                .setParameter("uuid", uuid)
                .getResultStream()
                .findFirst()
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        Long count = em.createQuery("SELECT COUNT(u) FROM UsuarioEntity u WHERE u.correo = :correo", Long.class)
                .setParameter("correo", correo)
                .getSingleResult();
        return count > 0;
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity;

        if (usuario.getId() != null) {
            // Para updates: cargar la entidad existente con sus permisos para no perderlos
            entity = em.createQuery(
                    "SELECT u FROM UsuarioEntity u LEFT JOIN FETCH u.permisosPersonalizados WHERE u.id = :id",
                    UsuarioEntity.class)
                    .setParameter("id", usuario.getId())
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + usuario.getId()));

            entity.setNombre(usuario.getNombre());
            entity.setApellido(usuario.getApellido());
            entity.setNombreUsuario(usuario.getNombreUsuario());
            entity.setCorreo(usuario.getCorreo());
            entity.setTelefono(usuario.getTelefono());
            entity.setActivo(usuario.isActivo());
            entity.setAlcanceDatos(usuario.getAlcanceDatos() != null ? usuario.getAlcanceDatos() : "TODAS");
            entity.setIdPlantaAsignada(usuario.getIdPlantaAsignada());
        } else {
            entity = UsuarioMapper.toEntity(usuario);
        }

        if (usuario.getIdRol() != null) {
            entity.setRol(em.getReference(RolEntity.class, usuario.getIdRol()));
        }

        // Actualizar permisos solo si el caller los envió explícitamente (non-null)
        if (usuario.getModulosPersonalizados() != null) {
            List<PermisoEntity> permisos = usuario.getModulosPersonalizados().isEmpty()
                    ? Collections.emptyList()
                    : em.createQuery(
                            "SELECT p FROM PermisoEntity p WHERE p.modulo IN :modulos",
                            PermisoEntity.class)
                            .setParameter("modulos", usuario.getModulosPersonalizados())
                            .getResultList();
            entity.getPermisosPersonalizados().clear();
            entity.getPermisosPersonalizados().addAll(permisos);
        }

        if (entity.getId() == null) {
            em.persist(entity);
        }
        em.flush();
        return UsuarioMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public void desactivar(Integer id) {
        em.createQuery("UPDATE UsuarioEntity u SET u.activo = false WHERE u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Integer, List<String>> findModulosPersonalizadosByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyMap();

        List<Object[]> rows = em.createNativeQuery(
                "SELECT DISTINCT up.id_usuario, p.modulo " +
                "FROM Usuario_Permiso up " +
                "JOIN Permiso p ON p.id = up.id_permiso " +
                "WHERE up.id_usuario IN (:ids)")
                .setParameter("ids", ids)
                .getResultList();

        Map<Integer, List<String>> result = new HashMap<>();
        for (Object[] row : rows) {
            Integer idUsuario = ((Number) row[0]).intValue();
            String modulo = (String) row[1];
            result.computeIfAbsent(idUsuario, k -> new ArrayList<>()).add(modulo);
        }
        return result;
    }

    private Usuario mapRow(Object[] row) {
        Usuario u = new Usuario();
        u.setId(((Number) row[0]).intValue());
        u.setUuid((String) row[1]);
        u.setNombre((String) row[2]);
        u.setApellido((String) row[3]);
        u.setCorreo((String) row[4]);
        u.setTelefono((String) row[5]);
        u.setIdEmpresa(((Number) row[6]).intValue());
        u.setNombreEmpresa((String) row[7]);
        u.setIdRol(((Number) row[8]).intValue());
        u.setNombreRol((String) row[9]);
        u.setActivo(row[10] instanceof Boolean ? (Boolean) row[10] : ((Number) row[10]).intValue() == 1);
        if (row[11] != null) {
            u.setUltimoAcceso(((java.sql.Timestamp) row[11]).toLocalDateTime());
        }
        return u;
    }
}
