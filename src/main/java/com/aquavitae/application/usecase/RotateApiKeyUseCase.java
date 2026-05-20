package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.ApiKeyInfo;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;

@ApplicationScoped
public class RotateApiKeyUseCase {

    public ApiKeyInfo execute(Integer id) {
        return new ApiKeyInfo(
                id,
                "API Key " + id,
                true,
                "2026-12-31",
                LocalDate.now().toString()
        );
    }
}