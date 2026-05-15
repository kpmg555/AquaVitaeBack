package com.aquavitae.domain.models;


public class UbicacionClima {
        private Integer id;
        private Double latitud;
        private Double longitud;
        private Integer elevation;

        public UbicacionClima(Integer id, Double latitud, Double longitud, Integer elevation) {
            this.id= id;
            this.latitud = latitud;
            this.longitud = longitud;
            this.elevation = elevation;
        }

        public Integer getId() {return id;}
        public  void setId(Integer id) {this.id = id;}

        public Double getLatitud() {return latitud;}
        public void setLatitud(Double latitud) {this.latitud = latitud;}

        public Double getLongitud() {return longitud;}
        public void setLongitud(Double longitud) {this.longitud = longitud;}

        public int getElevation() {return elevation;}
        public void setElevation(int elevation) {this.elevation = elevation;}

}
