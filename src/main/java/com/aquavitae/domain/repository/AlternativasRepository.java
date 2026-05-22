package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.AlertaOperativa;
import com.aquavitae.domain.models.AlternativaUbicacion;
import com.aquavitae.domain.models.FactorEvaluacion;

import java.util.List;

public interface AlternativasRepository {
    AlertaOperativa obtenerAlerta(Integer plantaId);
    List<AlternativaUbicacion> obtenerAlternativas(Integer plantaId);
    List<FactorEvaluacion> obtenerFactores(Integer plantaId);
}
