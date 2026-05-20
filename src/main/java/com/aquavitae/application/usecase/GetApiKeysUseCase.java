package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.ApiKeyInfo;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class GetApiKeysUseCase {

    public List<ApiKeyInfo> execute() {
        return List.of(
                new ApiKeyInfo(1, "Open-Meteo API Key", true, "2026-12-31", "2026-05-19"),
                new ApiKeyInfo(2, "NASA POWER API Key", true, "2026-12-31", "2026-05-19"),
                new ApiKeyInfo(3, "SMN API Key", false, "2026-06-30", "2026-05-10")
        );
    }
}