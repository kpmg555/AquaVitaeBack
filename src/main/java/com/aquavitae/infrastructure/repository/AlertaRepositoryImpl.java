package com.aquavitae.infrastructure.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.repository.AlertaRepository;
import com.aquavitae.infrastructure.entities.AlertaEntity;
import com.aquavitae.infrastructure.mapper.AlertaMapper;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AlertaRepositoryImpl implements AlertaRepository, PanacheRepositoryBase<AlertaEntity, Integer> {
    @Inject EntityManager em;

    @Override
    public long contarAltasActivas() {
        return em.createQuery(
                        "SELECT COUNT(a) FROM AlertaEntity a WHERE a.tipo = 'ALTO'",
                        Long.class)
                .getSingleResult();
    }

    @Override
    public long contarMediasActivas() {
        return em.createQuery(
                        "SELECT COUNT(a) FROM AlertaEntity a WHERE a.tipo = 'MEDIO'",
                        Long.class)
                .getSingleResult();
    }


    @Override
    public List<AlertaDominio> findRecientes(int limite) {
        return em.createQuery(
                        "SELECT a FROM AlertaEntity a ORDER BY a.fecha DESC",
                        AlertaEntity.class)
                .setMaxResults(limite)
                .getResultList()
                .stream()
                .map(AlertaMapper::toResumen)
                .collect(Collectors.toList());
    }
}
