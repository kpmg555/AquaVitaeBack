package com.aquavitae.domain.repository;
import com.aquavitae.domain.models.AlertaResumen;
import com.aquavitae.domain.models.PlantaRiesgo;
import java.util.List;

public interface AlertaRepository {
    long contarAltasActivas();
    long contarMediasActivas();
    List<AlertaResumen> findRecientes(int limite);
}
