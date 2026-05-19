package org.acme.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.acme.domain.models.Rol;
import org.acme.domain.repository.RolRepository;
import org.acme.infrastructure.entities.RolEntity;
import org.hibernate.Hibernate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class RolRepositoryImpl
        implements RolRepository, PanacheRepositoryBase<RolEntity, Integer> {

    @Inject EntityManager em;

    @Override
    public List<Rol> findAll() {
        return em.createQuery("SELECT r FROM RolEntity r", RolEntity.class)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Rol> findById(Integer id) {
        return em.createQuery("""
                SELECT r FROM RolEntity r
                LEFT JOIN FETCH r.permisos
                WHERE r.id = :id
                """, RolEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .map(this::toDomainConPermisos);
    }

    @Override
    public int countTotalPermisos() {
        return em.createQuery("""
                SELECT COUNT(rp) FROM RolEntity r JOIN r.permisos rp
                """, Long.class)
                .getSingleResult()
                .intValue();
    }

    private Rol toDomain(RolEntity entity) {
        Rol rol = new Rol();
        rol.setId(entity.getId());
        rol.setNombre(entity.getNombre());
        rol.setDescripcion(entity.getDescripcion());
        rol.setTotalPermisos(
            Hibernate.isInitialized(entity.getPermisos())
                ? entity.getPermisos().size() : 0);
        return rol;
    }

    private Rol toDomainConPermisos(RolEntity entity) {
        Rol rol = toDomain(entity);
        if (Hibernate.isInitialized(entity.getPermisos())) {
            rol.setPermisos(
                entity.getPermisos().stream()
                    .map(p -> p.getModulo())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList())
            );
            rol.setTotalPermisos(entity.getPermisos().size());
        }
        return rol;
    }
}