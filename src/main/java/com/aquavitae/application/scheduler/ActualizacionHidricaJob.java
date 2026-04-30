package com.aquavitae.application.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.aquavitae.application.usecase.ActualizadorHidricoService;

@ApplicationScoped
public class ActualizacionHidricaJob {

    @Inject
    ActualizadorHidricoService service;

    @Scheduled(every = "1h")
    void ejecutar() {
        service.ejecutarActualizacion();
    }
}