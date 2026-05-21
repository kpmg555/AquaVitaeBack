package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.ApiKeyInfo;
import com.aquavitae.domain.ports.ApiKeyRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RotateApiKeyUseCase {

    @Inject
    ApiKeyRepositoryPort repository;

    public ApiKeyInfo execute(Integer id) {
        return repository.rotate(id);
    }
}