package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.domain.models.Rol;
import com.aquavitae.domain.repository.RolRepository;
import com.aquavitae.infrastructure.entities.RolEntity;
import com.aquavitae.infrastructure.entities.PermisoEntity;
import org.hibernate.Hibernate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class RolRepositoryImpl implements RolRepository {

    @Inject
    EntityManager em;

    @Override
    public List<Rol> findAll() {
        return em.createQuery(
                "SELECT DISTINCT r FROM RolEntity r LEFT JOIN FETCH r.permisos",
                RolEntity.class)
                .getResultList()
                .stream()
                .map(this::toDomainWithPermisos)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Rol> findById(Integer id) {
        return em.createQuery("SELECT r FROM RolEntity r LEFT JOIN FETCH r.permisos WHERE r.id = :id", RolEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst()
                .map(this::toDomainWithPermisos);
    }

    @Override
    public int countTotalPermisos() {
        return em.createQuery("SELECT COUNT(rp) FROM RolEntity r JOIN r.permisos rp", Long.class)
                .getSingleResult()
                .intValue();
    }

    private Rol toDomain(RolEntity e) {
        Rol r = new Rol();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setDescripcion(e.getDescripcion());
        r.setTotalPermisos(Hibernate.isInitialized(e.getPermisos()) ? e.getPermisos().size() : 0);
        return r;
    }

    private Rol toDomainWithPermisos(RolEntity e) {
        Rol r = toDomain(e);
        if (Hibernate.isInitialized(e.getPermisos())) {
            r.setPermisos(e.getPermisos().stream()
                    .map(PermisoEntity::getModulo)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList()));
            r.setTotalPermisos(e.getPermisos().size());
        }
        return r;
    }
}