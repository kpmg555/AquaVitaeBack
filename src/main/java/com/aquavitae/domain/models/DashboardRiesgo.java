package com.aquavitae.domain.models;

import java.util.List;

public class DashboardRiesgo {

        private final List<PlantaRiesgo> plantas;
        private  final ResumenRiesgo resumen;

        public DashboardRiesgo(List<PlantaRiesgo> plantas, ResumenRiesgo resumen) {
            this.plantas = plantas;
            this.resumen = resumen;
        }

        public List<PlantaRiesgo> getPlantas() {return plantas;}

        public ResumenRiesgo getResumen() {return resumen;}
}
