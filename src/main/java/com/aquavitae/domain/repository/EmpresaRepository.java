package com.aquavitae.domain.repository;

import com.aquavitae.application.dto.EmpresaDto;
import java.util.List;

public interface EmpresaRepository {
    List<EmpresaDto> listarTodas();
}