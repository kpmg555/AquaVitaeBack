package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.ApiKeyInfo;
import com.aquavitae.domain.ports.ApiKeyRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class GetApiKeysUseCase {

    @Inject
    ApiKeyRepositoryPort repository;

    public List<ApiKeyInfo> execute() {
        return repository.findAll();
    }
}