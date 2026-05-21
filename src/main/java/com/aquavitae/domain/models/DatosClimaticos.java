package com.aquavitae.domain.models;

public class DatosClimaticos {

    //final porque no se pueden modificar los datos climáticos una vez creados,
    private final Integer id;
    private final Double precipitacionMm;
    private final Double humedadSuelo0_1;
    private final Double humedadSuelo1_3;
    private final Double humedadSuelo3_9;
    private final Double humedadSuelo;
    private final Double evapotranspiracion;
    private final Double temperatura;

    public DatosClimaticos(Integer id,
                           Double precipitacionMm,
                           Double humedadSuelo0_1,
                           Double humedadSuelo1_3,
                           Double humedadSuelo3_9,
                           Double evapotranspiracion,
                           Double temperatura) {

        this.id = id;
        this.precipitacionMm = precipitacionMm;
        this.humedadSuelo0_1 = humedadSuelo0_1;
        this.humedadSuelo1_3 = humedadSuelo1_3;
        this.humedadSuelo3_9 = humedadSuelo3_9;
        this.evapotranspiracion = evapotranspiracion;
        this.temperatura = temperatura;

        this.humedadSuelo = (
                humedadSuelo0_1 +
                        humedadSuelo1_3 +
                        humedadSuelo3_9
        ) / 3;
    }

    public Integer getId() { return id; }

    public Double getPrecipitacionMm() { return precipitacionMm; }

    public Double getHumedadSuelo0_1() { return humedadSuelo0_1; }

    public Double getHumedadSuelo1_3() { return humedadSuelo1_3; }

    public Double getHumedadSuelo3_9() { return humedadSuelo3_9; }

    public Double getHumedadSuelo() { return humedadSuelo; }

    public Double getEvapotranspiracion() { return evapotranspiracion; }

    public Double getTemperatura() { return temperatura; }
}