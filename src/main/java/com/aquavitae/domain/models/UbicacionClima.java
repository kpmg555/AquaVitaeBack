package com.aquavitae.domain.models;


public class UbicacionClima {
        private Integer id;
        private float latitud;
        private float longitud;
        private int elevation;

        public UbicacionClima(Integer id, float latitud, float longitud, int elevation) {
            this.id= id;
            this.latitud = latitud;
            this.longitud = longitud;
            this.elevation = elevation;
        }

        public Integer getId() {return id;}
        public  void setId(Integer id) {this.id = id;}

        public float getLatitud() {return latitud;}
        public void setLatitud(float latitud) {this.latitud = latitud;}

        public float getLongitud() {return longitud;}
        public void setLongitud(float longitud) {this.longitud = longitud;}

        public int getElevation() {return elevation;}
        public void setElevation(int elevation) {this.elevation = elevation;}

}
