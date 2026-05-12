package com.aquavitae.infrastructure.repository;

import com.aquavitae.infrastructure.mapper.AlertaMapper;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.domain.repository.AlertaRepository;
import com.aquavitae.infrastructure.entities.AlertaEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AlertaRepositoryImpl implements AlertaRepository, PanacheRepositoryBase<AlertaEntity, Integer> {

    @Override
    public List<AlertaDominio> findRecientes(int limit) {
        List<AlertaEntity> entities = find("ORDER BY fecha DESC")
                .page(0, limit).list();
        return entities.stream()
                .map(AlertaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void save(AlertaDominio alerta) {
        AlertaEntity entity = new AlertaEntity();
        entity.setTipo(alerta.getTipo());
        entity.setTitulo(alerta.getTitulo());
        entity.setDescripcion(alerta.getDescripcion());
        entity.setNivelActual(alerta.getNivelActual());
        entity.setUmbral(alerta.getUmbral());          // NUEVO
        entity.setFecha(alerta.getFecha() != null ? alerta.getFecha() : LocalDateTime.now());
        persist(entity);
    }
}