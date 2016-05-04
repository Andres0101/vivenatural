package com.example.andres_bonilla.ensayo.activity.classes;

public class SuccessReserve {

    private String producto;
    private String reservadoPor;
    private String reservadoA;
    private double cantidadReservada;
    private int precio;

    public SuccessReserve() {}

    public SuccessReserve(String producto, String reservadoPor, String reservadoA, double cantidadReservada, int precio) {
        this.producto = producto;
        this.reservadoPor = reservadoPor;
        this.reservadoA = reservadoA;
        this.cantidadReservada = cantidadReservada;
        this.precio = precio;
    }

    public String getProducto() {
        return producto;
    }

    public String getReservadoPor() {
        return reservadoPor;
    }

    public String getReservadoA() {
        return reservadoA;
    }

    public double getCantidadReservada() {
        return cantidadReservada;
    }

    public int getPrecio() {
        return precio;
    }
}
