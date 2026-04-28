package com.aquavitae.domain.repository;
import com.aquavitae.domain.models.Alerta;

import java.util.List;

public interface AlertaRepository {
    long contarAltasActivas();
    long contarMediasActivas();
    List<Alerta> findRecientes(int limite);
}
