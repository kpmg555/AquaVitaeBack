package org.acme.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.acme.domain.models.Usuario;
import org.acme.domain.repository.UsuarioRepository;
import org.acme.infrastructure.entities.RolEntity;
import org.acme.infrastructure.entities.UsuarioEntity;
import org.acme.infrastructure.mapper.UsuarioMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class UsuarioRepositoryImpl
        implements UsuarioRepository, PanacheRepositoryBase<UsuarioEntity, Integer> {

    @Inject EntityManager em;

    private static final String QUERY_USUARIOS_CON_ACCESO_Y_ALCANCE = """
            SELECT
                u,
                (SELECT MAX(s.fechaInicio)
                 FROM Sesion s
                 WHERE s.idUsuario = u.id AND s.exitosa = true),
                (SELECT p.nombre
                 FROM PlantaEntity p
                 JOIN p.id IN (
                     SELECT rp.idPlanta FROM RolPlantaEntity rp WHERE rp.idRol = u.rol.id
                 )
                 ORDER BY p.nombre ASC
                 LIMIT 1),
                (SELECT r.nombre
                 FROM RegionEntity r
                 JOIN r.id IN (
                    SELECT rr.idRegion FROM RolRegionEntity rr WHERE rr.idRol = u.rol.id
                 )
                 ORDER BY r.nombre ASC
                 LIMIT 1)
            FROM UsuarioEntity u
            JOIN FETCH u.rol
            WHERE u.idEmpresa = :idEmpresa
              AND u.activo = true
            ORDER BY u.nombre, u.apellido
            """;

    private static final String SQL_USUARIOS = """
            SELECT
                u.id,
                u.uuid,
                u.nombre,
                u.apellido,
                u.correo,
                u.telefono,
                u.id_empresa,
                u.id_rol,
                u.activo,
                r.nombre AS nombre_rol,
                MAX(s.fecha_inicio) AS ultimo_acceso,
                p.nombre AS nombre_planta,
                reg.nombre AS nombre_region
            FROM Usuario u
            JOIN Rol r ON r.id = u.id_rol
            LEFT JOIN Sesion s
                ON  s.id_usuario = u.id
                AND s.exitosa = true
            LEFT JOIN Rol_Planta rp ON rp.id_rol = u.id_rol
            LEFT JOIN Planta p  ON p.id= rp.id_planta AND p.activa = true
            LEFT JOIN Rol_Region rr ON rr.id_rol = u.id_rol
            LEFT JOIN Region reg ON reg.id = rr.id_region
            WHERE u.id_empresa = :idEmpresa
              AND u.activo= true
            GROUP BY
                u.id, u.uuid, u.nombre, u.apellido, u.correo,
                u.telefono, u.id_empresa, u.id_rol, u.activo,
                r.nombre, p.nombre, reg.nombre
            ORDER BY u.nombre, u.apellido
            LIMIT :size OFFSET :offset
            """;

    private static final String SQL_COUNT = """
            SELECT COUNT(*)
            FROM Usuario u
            WHERE u.id_empresa = :idEmpresa AND u.activo = true
            """;

    private static final String SQL_COUNT_TOTAL = """
            SELECT COUNT(*)
            FROM Usuario u
            WHERE u.id_empresa = :idEmpresa
            """;

    private static final int IDX_ID = 0;
    private static final int IDX_UUID= 1;
    private static final int IDX_NOMBRE = 2;
    private static final int IDX_APELLIDO= 3;
    private static final int IDX_CORREO= 4;
    private static final int IDX_TELEFONO = 5;
    private static final int IDX_ID_EMPRESA = 6;
    private static final int IDX_ID_ROL= 7;
    private static final int IDX_ACTIVO = 8;
    private static final int IDX_NOMBRE_ROL = 9;
    private static final int IDX_ULTIMO_ACC = 10;
    private static final int IDX_PLANTA = 11;
    private static final int IDX_REGION  = 12;

    @Override
    @SuppressWarnings("unchecked")
    public List<Usuario> findActivos(Integer idEmpresa, int page, int size) {
        List<Object[]> rows = em.createNativeQuery(SQL_USUARIOS)
                .setParameter("idEmpresa", idEmpresa)
                .setParameter("size", size)
                .setParameter("offset", page * size)
                .getResultList();

        return rows.stream()
                .map(this::mapRow)
                .collect(Collectors.toList());
    }

    @Override
    public long countActivos(Integer idEmpresa) {
        return ((Number) em.createNativeQuery(SQL_COUNT)
                .setParameter("idEmpresa", idEmpresa)
                .getSingleResult()).longValue();
    }

    @Override
    public long countTotal(Integer idEmpresa) {
        return ((Number) em.createNativeQuery(SQL_COUNT_TOTAL)
                .setParameter("idEmpresa", idEmpresa)
                .getSingleResult()).longValue();
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return em.createQuery("""
                SELECT u FROM UsuarioEntity u
                LEFT JOIN FETCH u.rol
                WHERE u.id = :id
                """, UsuarioEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByUuid(String uuid) {
        return em.createQuery("""
                SELECT u FROM UsuarioEntity u
                LEFT JOIN FETCH u.rol
                WHERE u.uuid = :uuid
                """, UsuarioEntity.class)
                .setParameter("uuid", uuid)
                .getResultStream()
                .findFirst()
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        Long count = em.createQuery("""
                SELECT COUNT(u) FROM UsuarioEntity u WHERE u.correo = :correo
                """, Long.class)
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
        u.setId(((Number) row[IDX_ID]).intValue());
        u.setUuid((String) row[IDX_UUID]);
        u.setNombre((String)row[IDX_NOMBRE]);
        u.setApellido((String)row[IDX_APELLIDO]);
        u.setCorreo((String)row[IDX_CORREO]);
        u.setTelefono((String)row[IDX_TELEFONO]);

        if (row[IDX_ID_EMPRESA] != null)
            u.setIdEmpresa(((Number) row[IDX_ID_EMPRESA]).intValue());

        if (row[IDX_ID_ROL] != null)
            u.setIdRol(((Number) row[IDX_ID_ROL]).intValue());

        u.setActivo(row[IDX_ACTIVO] != null && ((Number) row[IDX_ACTIVO]).intValue() == 1);
        u.setNombreRol((String) row[IDX_NOMBRE_ROL]);

        if (row[IDX_ULTIMO_ACC] != null) {
            u.setUltimoAcceso(
                ((java.sql.Timestamp) row[IDX_ULTIMO_ACC]).toLocalDateTime());
        }

        String nombrePlanta = (String) row[IDX_PLANTA];
        String nombreRegion= (String) row[IDX_REGION];

        if (nombrePlanta != null && !nombrePlanta.isBlank()) {
            u.setAlcance("Planta " + nombrePlanta);
        } else if (nombreRegion != null && !nombreRegion.isBlank()) {
            u.setAlcance("Región " + nombreRegion);
        } else {
            u.setAlcance("Todas las regiones y plantas");
        }

        return u;
    }
}