package com.example.andres_bonilla.ensayo.activity.classes;

public class Rate {
    private String calificadoPor;
    private String calificoA;
    private int calificacion;

    public Rate() {}

    public Rate(String calificadoPor, String calificoA, int calificacion) {
        this.calificadoPor = calificadoPor;
        this.calificoA = calificoA;
        this.calificacion = calificacion;
    }

    public String getCalificadoPor() {
        return calificadoPor;
    }

    public String getCalificoA() {
        return calificoA;
    }

    public int getCalificacion() {
        return calificacion;
    }
}
