package com.aquavitae.infrastructure.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import com.aquavitae.application.dto.EmpresaDto;
import com.aquavitae.domain.repository.EmpresaRepository;
import java.util.List;

@ApplicationScoped
public class EmpresaRepositoryImpl implements EmpresaRepository {

    @Inject
    EntityManager em;

    @Override
    public List<EmpresaDto> listarTodas() {
        return em
                .createQuery("SELECT new com.aquavitae.application.dto.EmpresaDto(e.id, e.nombre) FROM EmpresaEntity e",
                        EmpresaDto.class)
                .getResultList();
    }
}