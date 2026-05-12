package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.infrastructure.entities.AlertaEntity;

public class AlertaMapper {

    public static AlertaDominio toDomain(AlertaEntity entity) {
        return new AlertaDominio(
                entity.getId(),
                entity.getTipo(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getNivelActual(),
                entity.getUmbral(),
                entity.getFecha()
        );
    }

    public static AlertaEntity toEntity(AlertaDominio domain) {
        AlertaEntity entity = new AlertaEntity();
        entity.setTipo(domain.getTipo());
        entity.setTitulo(domain.getTitulo());
        entity.setDescripcion(domain.getDescripcion());
        entity.setNivelActual(domain.getNivelActual());
        entity.setFecha(domain.getFecha());
        return entity;
    }
}