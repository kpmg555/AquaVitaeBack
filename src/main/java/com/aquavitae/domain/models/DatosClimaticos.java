package com.aquavitae.domain.models;

public class DatosClimaticos {

    //final porque no se pueden modificar los datos climáticos una vez creados,
    private final Integer id;
    private final float precipitacionMm;
    private final float humedadSuelo0_1;
    private final float humedadSuelo1_3;
    private final float humedadSuelo3_9;
    private final float humedadSuelo;
    private final float evapotranspiracion;
    private final float temperatura;

    public DatosClimaticos(Integer id,
                           float precipitacionMm,
                           float humedadSuelo0_1,
                           float humedadSuelo1_3,
                           float humedadSuelo3_9,
                           float evapotranspiracion,
                           float temperatura) {

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
        ) / 3f;
    }

    public Integer getId() { return id; }

    public float getPrecipitacionMm() { return precipitacionMm; }

    public float getHumedadSuelo0_1() { return humedadSuelo0_1; }

    public float getHumedadSuelo1_3() { return humedadSuelo1_3; }

    public float getHumedadSuelo3_9() { return humedadSuelo3_9; }

    public float getHumedadSuelo() { return humedadSuelo; }

    public float getEvapotranspiracion() { return evapotranspiracion; }

    public float getTemperatura() { return temperatura; }
}