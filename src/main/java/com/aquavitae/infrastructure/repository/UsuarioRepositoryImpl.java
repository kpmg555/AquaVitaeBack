package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import com.aquavitae.domain.models.Usuario;
import com.aquavitae.domain.repository.UsuarioRepository;
import com.aquavitae.infrastructure.entities.RolEntity;
import com.aquavitae.infrastructure.entities.UsuarioEntity;
import com.aquavitae.infrastructure.mapper.UsuarioMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UsuarioRepositoryImpl implements UsuarioRepository {

    @Inject
    EntityManager em;

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
    public List<Usuario> findActivosConDetalle(Integer idEmpresa, int page, int size) {
        List<Object[]> rows = em.createNativeQuery(SQL_USUARIOS)
                .setParameter("idEmpresa", idEmpresa)
                .setParameter("size", size)
                .setParameter("offset", page * size)
                .getResultList();
        return rows.stream().map(this::mapRow).collect(Collectors.toList());
    }

    @Override
    public long countActivos(Integer idEmpresa) {
        return ((Number) em.createNativeQuery(SQL_COUNT).setParameter("idEmpresa", idEmpresa).getSingleResult())
                .longValue();
    }

    @Override
    public long countTotal(Integer idEmpresa) {
        return ((Number) em.createNativeQuery(SQL_COUNT_TOTAL).setParameter("idEmpresa", idEmpresa).getSingleResult())
                .longValue();
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        UsuarioEntity entity = em.find(UsuarioEntity.class, id);
        return Optional.ofNullable(entity).map(UsuarioMapper::toDomain);
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
        UsuarioEntity entity = UsuarioMapper.toEntity(usuario);
        if (usuario.getIdRol() != null) {
            entity.setRol(em.getReference(RolEntity.class, usuario.getIdRol()));
        }
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            entity = em.merge(entity);
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
        u.setActivo(((Number) row[10]).intValue() == 1);
        if (row[11] != null) {
            u.setUltimoAcceso(((java.sql.Timestamp) row[11]).toLocalDateTime());
        }
        return u;
    }
}