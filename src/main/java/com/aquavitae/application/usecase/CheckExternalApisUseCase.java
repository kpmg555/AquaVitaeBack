package com.aquavitae.application.usecase;

import com.aquavitae.domain.models.UbicacionClima;
import com.aquavitae.domain.ports.FuenteClimaPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;

@ApplicationScoped
public class CheckExternalApisUseCase {

    @Inject
    @Named("openMeteo")
    FuenteClimaPort openMeteo;

    @Inject
    @Named("nasaPower")
    FuenteClimaPort nasaPower;

    @Inject
    @Named("smn")
    FuenteClimaPort smn;

    public void execute() {

        List<UbicacionClima> ubicaciones = List.of(
                new UbicacionClima(
                        1,
                        19.4326,
                        -99.1332,
                        1
                )
        );

        try {
            openMeteo.obtenerDatos(ubicaciones);
        } catch (Exception ignored) {}

        try {
            nasaPower.obtenerDatos(ubicaciones);
        } catch (Exception ignored) {}

        try {
            smn.obtenerDatos(ubicaciones);
        } catch (Exception ignored) {}
    }
}