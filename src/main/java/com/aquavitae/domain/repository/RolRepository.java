package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.Rol;
import java.util.List;
import java.util.Optional;

public interface RolRepository {
    List<Rol> findAll();

    Optional<Rol> findById(Integer id);

    int countTotalPermisos();
}