package com.aquavitae.interfaces.rest;

import com.aquavitae.application.dto.KpiSimulacionDto;
import com.aquavitae.application.dto.ProyeccionDto;
import com.aquavitae.infrastructure.entities.PrediccionHidricaEntity;
import com.aquavitae.infrastructure.entities.PrediccionKpiEntity;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Sirve las predicciones ARIMA (tabla Prediccion_Hidrica) con la MISMA forma
 * que /api/simulacion/proyeccion, para que las gráficas las consuman sin cambios.
 */
@Path("/api/predicciones")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Predicciones (ARIMA)", description = "Predicciones hídricas calculadas por el modelo ARIMA, con la misma forma que la simulación para reutilizar las gráficas.")
public class PrediccionResource {

    @Inject
    EntityManager em;

    @GET
    @Path("/proyeccion")
    @Transactional
    @Operation(
            summary = "Proyección ARIMA",
            description = "Devuelve la última proyección ARIMA del índice hídrico para una planta, con bandas de confianza. Misma forma que /api/simulacion/proyeccion."
    )
    public ProyeccionDto getProyeccion(
            @Parameter(description = "ID de la planta a consultar")
            @QueryParam("plantaId") Integer plantaId) {
        List<PrediccionHidricaEntity> preds = em.createQuery(
                "SELECT p FROM PrediccionHidricaEntity p " +
                "WHERE p.idPlanta = :pid AND p.fechaCalculo = (" +
                "  SELECT MAX(p2.fechaCalculo) FROM PrediccionHidricaEntity p2 WHERE p2.idPlanta = :pid) " +
                "ORDER BY p.horizonteDia", PrediccionHidricaEntity.class)
                .setParameter("pid", plantaId)
                .getResultList();

        List<ProyeccionDto.PuntoDto> puntos = new ArrayList<>();
        List<ProyeccionDto.PuntoDto> bandaSuperior = new ArrayList<>();
        List<ProyeccionDto.PuntoDto> bandaInferior = new ArrayList<>();
        int peakDay = 0;
        float peakValue = 0f;

        for (PrediccionHidricaEntity p : preds) {
            int dia = p.getHorizonteDia();
            float valor = (float) (p.getValorPredicho() * 100.0);              // índice 0-1 -> %
            double sup = (p.getBandaSuperior() != null ? p.getBandaSuperior() : p.getValorPredicho());
            double inf = (p.getBandaInferior() != null ? p.getBandaInferior() : p.getValorPredicho());

            puntos.add(new ProyeccionDto.PuntoDto(dia, valor));
            bandaSuperior.add(new ProyeccionDto.PuntoDto(dia, (float) (sup * 100.0)));
            bandaInferior.add(new ProyeccionDto.PuntoDto(dia, (float) (inf * 100.0)));

            if (valor > peakValue) {
                peakValue = valor;
                peakDay = dia;
            }
        }

        int startDay = puntos.isEmpty() ? 0 : puntos.get(0).getDia();
        return new ProyeccionDto(puntos, bandaSuperior, bandaInferior, startDay, peakDay, peakValue);
    }

    @GET
    @Path("/kpis")
    @Transactional
    @Operation(
            summary = "KPIs de la predicción ARIMA",
            description = "Devuelve los KPIs más recientes calculados por el modelo ARIMA para una planta (índice actual, días hasta umbral, probabilidad de evento y pérdida estimada)."
    )
    public KpiSimulacionDto getKpis(
            @Parameter(description = "ID de la planta a consultar")
            @QueryParam("plantaId") Integer plantaId) {
        List<PrediccionKpiEntity> rows = em.createQuery(
                "SELECT k FROM PrediccionKpiEntity k " +
                "WHERE k.idPlanta = :pid ORDER BY k.fechaCalculo DESC", PrediccionKpiEntity.class)
                .setParameter("pid", plantaId)
                .setMaxResults(1)
                .getResultList();

        if (rows.isEmpty()) {
            return new KpiSimulacionDto(0f, -1, 0f, 0d);
        }
        PrediccionKpiEntity k = rows.get(0);
        return new KpiSimulacionDto(
                k.getIndiceActual() != null ? k.getIndiceActual() : 0f,
                k.getDiasHastaUmbral() != null ? k.getDiasHastaUmbral() : -1,
                k.getProbabilidadEvento() != null ? k.getProbabilidadEvento() : 0f,
                k.getPerdidaEstimada() != null ? k.getPerdidaEstimada() : 0d);
    }
}
