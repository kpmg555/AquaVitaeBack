package com.aquavitae.domain.repository;
import com.aquavitae.domain.models.AlertaDominio;
import java.util.List;

public interface AlertaRepository {
    List<AlertaDominio> findRecientes(int limit);
    void save(AlertaDominio alerta);
}
