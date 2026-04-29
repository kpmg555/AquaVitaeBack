package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.AlertaDominio;
import com.aquavitae.infrastructure.entities.AlertaEntity;

public class AlertaMapper {

    public static AlertaDominio toResumen(AlertaEntity e) {
        AlertaDominio r = new AlertaDominio();
        r.setId(e.getId());
        r.setTipo(e.getTipo());
        r.setTitulo(e.getTitulo());
        r.setDescripcion(e.getDescripcion());
        r.setFecha(e.getFecha());
        return r;
    }
}