package com.aquavitae.domain.repository;
import com.aquavitae.domain.models.AlertaDominio;


public interface AlertaRepository {
    AlertaDominio save(AlertaDominio alerta);
}
