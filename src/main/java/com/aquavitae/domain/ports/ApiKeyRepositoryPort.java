package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.ApiKeyInfo;

import java.util.List;

public interface ApiKeyRepositoryPort {
    List<ApiKeyInfo> findAll();
    ApiKeyInfo rotate(Integer id);
}