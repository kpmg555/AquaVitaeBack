package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.AlertaResumen;
import com.aquavitae.infrastructure.entities.AlertaEntity;

public class AlertaMapper {

    public static AlertaResumen toResumen(AlertaEntity e) {
        AlertaResumen r = new AlertaResumen();
        r.setId(e.getId());
        r.setTipo(e.getTipo());
        r.setTitulo(e.getTitulo());
        r.setDescripcion(e.getDescripcion());
        r.setFecha(e.getFecha());
        return r;
    }
}