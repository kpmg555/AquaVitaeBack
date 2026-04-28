package com.aquavitae.infrastructure.mapper;

import com.aquavitae.domain.models.Alerta;
import com.aquavitae.infrastructure.entities.AlertaEntity;

public class AlertaMapper {

    public static Alerta toResumen(AlertaEntity e) {
        Alerta r = new Alerta();
        r.setId(e.getId());
        r.setTipo(e.getTipo());
        r.setTitulo(e.getTitulo());
        r.setDescripcion(e.getDescripcion());
        r.setFecha(e.getFecha());
        return r;
    }
}