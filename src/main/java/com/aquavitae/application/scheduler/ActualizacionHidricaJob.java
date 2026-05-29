package com.aquavitae.application.scheduler;

import com.aquavitae.application.usecase.ActualizadorHidricoService;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ActualizacionHidricaJob {

    @Inject
    ActualizadorHidricoService service;

    @Scheduled(every = "1h")
    @Transactional
    void ejecutar() {
        service.ejecutarActualizacion();
    }
}