package com.aquavitae.domain.models;

import java.util.List;

public class DashboardRiesgo {

        private List<PlantaRiesgo> plantas;
        private ResumenRiesgo resumen;

        public DashboardRiesgo(List<PlantaRiesgo> plantas, ResumenRiesgo resumen) {
            this.plantas = plantas;
            this.resumen = resumen;
        }

        public List<PlantaRiesgo> getPlantas() {return plantas;}
        public void setPlantas(List<PlantaRiesgo> plantas) {this.plantas = plantas;}

        public ResumenRiesgo getResumen() {return resumen;}
        public void setResumen(ResumenRiesgo resumen) {this.resumen = resumen;}

}
