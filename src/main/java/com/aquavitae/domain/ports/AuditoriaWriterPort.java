package com.aquavitae.domain.ports;

public interface AuditoriaWriterPort {

    void registrar(
            Integer idUsuario,
            String accion,
            String modulo,
            String entidad,
            String descripcion,
            String ip,
            String severidad,
            String valorAnterior,
            String valorNuevo
    );
}