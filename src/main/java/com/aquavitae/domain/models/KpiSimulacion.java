package com.aquavitae.domain.models;

public class KpiSimulacion {

    private final float indiceHidricoActual;
    private final int diasHastaUmbralCritico;
    private final float probabilidadEventoCritico;
    private final double perdidaEconomicaProyectada;

    public KpiSimulacion(float indiceHidricoActual,
                         int diasHastaUmbralCritico,
                         float probabilidadEventoCritico,
                         double perdidaEconomicaProyectada) {
        this.indiceHidricoActual = indiceHidricoActual;
        this.diasHastaUmbralCritico = diasHastaUmbralCritico;
        this.probabilidadEventoCritico = probabilidadEventoCritico;
        this.perdidaEconomicaProyectada = perdidaEconomicaProyectada;
    }

    public float getIndiceHidricoActual() { return indiceHidricoActual; }
    public int getDiasHastaUmbralCritico() { return diasHastaUmbralCritico; }
    public float getProbabilidadEventoCritico() { return probabilidadEventoCritico; }
    public double getPerdidaEconomicaProyectada() { return perdidaEconomicaProyectada; }
}
